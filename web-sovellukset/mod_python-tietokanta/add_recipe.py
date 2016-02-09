# -*- coding: utf-8 -*-

from mod_python import apache, Session, util as modutil
from xml.dom import minidom, Node
import os
import sys

moduulit = "/nashome3/saelosmo/html/hidden/moduulit"
resurssit = "/nashome3/saelosmo/html/tiea218/teht4"
sys.path.append(moduulit)
sys.path.append(resurssit)
import db_utils as dbutil
import dom_utils as domutil

templatepath = os.path.join(resurssit, "templates")
addpath = os.path.join(templatepath, "add_recipe.xhtml")
editpath = os.path.join(templatepath, "edit_recipe.xhtml")


def get_form_data(req):
	"""Hae pyynnöstä lomakkeen data.

	Returns:
		Data dict-muodossa. Avaimet: nimi (str), kuvaus (str), maara (str), laji (str), tekstit (str[])
	"""
	data = {}
	data["nimi"] = req.form.getfirst("nimi").strip()
	data["kuvaus"] = req.form.getfirst("kuvaus")
	data["maara"] = req.form.getfirst("maara").strip()
	data["laji"] = req.form.getfirst("laji")
	data["tekstit"] = req.form.getlist("teksti")
	return data

def validate_form(data):
	"""Validoi datan tiedot.

	Args:
		data: Data dict-muodossa. Avaimet: nimi (str), kuvaus (str), maara (str), laji (str), tekstit (str[])

	Returns:
		Jos kaikki ok, None. Muuten dictionary, jossa dataa vastaavissa avaimissa virheviestit.
	"""
	data_ok = True
	errors = {"nimi": "", "kuvaus": "", "maara": "", "laji": ""}

	if data["nimi"] == "":
		data_ok = False
		errors["nimi"] = u"Anna jokin nimi!"
	try:
		lkm = int(data["maara"])
		if lkm < 1:
			data_ok = False
			errors["maara"] = u"Henkilömäärän pitäisi olla ainakin 1!"
	except:
		data_ok = False
		errors["maara"] = u"Anna numero henkilömääräksi!"

	if not data_ok:
		return errors
	return None

def fill_form(data, dom):
	"""Täytä sivun lomake datan tiedoilla.

	Args:
		data: Data dict-muodossa. Avaimet: nimi (str), kuvaus (str), maara (str), laji (str), tekstit (str[])
	"""
	domutil.getContentElementById("nimi", dom).setAttribute("value", data["nimi"].decode("utf-8"))
	domutil.getContentElementById("kuvaus", dom).appendChild(dom.createTextNode(data["kuvaus"].decode("utf-8")))
	domutil.getContentElementById("maara", dom).setAttribute("value", data["maara"].decode("utf-8"))
	# Hae ja aseta viimeksi valittu ruokalaji.
	for option in domutil.getContentElementById("laji", dom).childNodes:
		if option.getAttribute("value") == data["laji"]:
			option.setAttribute("selected", "selected")
	# Palauta ohjetekstit.
	dom_tekstit = domutil.getContentElementById("vaiheet", dom).childNodes
	tekstit = data["tekstit"]
	for i in xrange(0, len(tekstit)):
		teksti = tekstit[i]
		dom_teksti = dom_tekstit[i].firstChild
		dom_teksti.appendChild(dom.createTextNode(teksti.decode("utf-8")))

def fill_errors(errors, dom):
	"""Täytä errors-dictin avaimien nimiä vastaavat DOM-elementit avaimia vastaavalla virheilmoituksella.

	Args:
		errors: Virheilmoitukset dict-muodossa. Avaimet: nimi (str), kuvaus (str), maara (str), laji (str), tekstit (str[]). HUOM oletetaan, että löytyy virhe-elementit muotoa avain+"_err"
	"""
	for error in errors.keys():
		errname = "%s_err" % error
		dom_err = domutil.getContentElementById(errname, dom)
		dom_err.appendChild(dom.createTextNode(errors[error]))

def db_insert(data, con):
	"""Lisää resepti tietokantaan datan tiedoilla.

	Args:
		data: Data dict-muodossa. Avaimet: nimi (str), kuvaus (str), maara (str), laji (str), tekstit (str[])

	Raises:
		Joku poikkeus jos tulee ongelmia.
	"""
	cur = con.cursor()
	result = None

	try:
		cur.execute("begin")
		cmd = """
			insert into resepti (nimi, kuvaus, henkilomaara, ruokalajiid)
			values (?, ?, ?, ?);
		"""
		cur.execute(cmd, (data["nimi"], data["kuvaus"], int(data["maara"]), int(data["laji"])))

		cmd = """
			insert into ohje (vaihenro, reseptiid, ohjeteksti)
			values (?, ?, ?);
		"""
		resepti_id = cur.lastrowid
		vaihenro = 1
		for teksti in data["tekstit"]:
			# Tyhjät vaiheet ohitetaan/poistetaan
			if teksti.strip() == "":
				continue
			cur.execute(cmd, (vaihenro, resepti_id, teksti))
			vaihenro += 1

		cur.execute("commit")
	except Exception, e:
		cur.execute("rollback")
		raise

def db_update(recipe_id, data, con):
	"""Päivitä recipe_id:tä vastaava resepti tietokantaan datan tiedoilla.

	Args:
		data: Data dict-muodossa. Avaimet: nimi (str), kuvaus (str), maara (str), laji (str), tekstit (str[])

	Raises:
		Joku poikkeus jos tulee ongelmia.
	"""
	cur = con.cursor()
	result = None

	try:
		cur.execute("begin")
		cmd = """
			update resepti
			set nimi = ?, kuvaus = ?, henkilomaara = ?, ruokalajiid = ?
			where reseptiid = ?;
		"""
		cur.execute(cmd, (data["nimi"], data["kuvaus"], int(data["maara"]), int(data["laji"]), recipe_id))

		# Aiemmat vaiheet poistetaan.
		cmd = """
			delete
			from ohje
			where reseptiid = ?;
		"""
		cur.execute(cmd, (recipe_id,))

		# Uudet vaiheet lisätään järjestyksessä.
		cmd = """
			insert into ohje (vaihenro, reseptiid, ohjeteksti)
			values (?, ?, ?);
		"""
		vaihenro = 1
		for teksti in data["tekstit"]:
			# Tyhjät vaiheet ohitetaan/poistetaan
			if teksti.strip() == "":
				continue
			cur.execute(cmd, (vaihenro, recipe_id, teksti))
			vaihenro += 1

		cur.execute("commit")
	except Exception, e:
		cur.execute("rollback")
		raise

def db_fetch(recipe_id, con):
	"""Hae recipe_id:tä vastaavan reseptin tiedot tietokannasta.

	Returns:
		Data dict-muodossa. Avaimet: nimi (str), kuvaus (str), maara (str), laji (str), tekstit (str[])

	Raises:
		Joku poikkeus jos tulee ongelmia.
	"""
	cur = con.cursor()
	data = {}

	cmd = """
		select Nimi as nimi, kuvaus as kuvaus, henkilomaara as maara, ruokalajiid as laji
		from resepti
		where reseptiid = ?;
	"""
	cur.execute(cmd, (recipe_id,))
	row = cur.fetchone()
	for key in row.keys():
		data[key] = str(row[key])

	cmd = """
		select oh.ohjeteksti as teksti
		from resepti re, ohje oh
		where re.reseptiid = oh.reseptiid
			and re.reseptiid = ?;
	"""
	cur.execute(cmd, (recipe_id,))
	tekstit = []
	for row in cur:
		tekstit.append(row[0])
	data["tekstit"] = tekstit

	return data

def insert(req, dom, con):
	"""Lisää uusi resepti tietokantaan req:n lomakkeen tiedoilla.

	Myös syötteet tarkistetaan ja lomakkeen virheilmoitukset asetetaan.

	Returns:
		True, jos onnistui.
	"""
	success = True
	data = get_form_data(req)
	errors = validate_form(data)
	if errors != None:
		fill_form(data, dom)
		fill_errors(errors, dom)
		return
	try:
		db_insert(data, con)
		dom_statusmsg = domutil.getContentElementById("status_text", dom)
		dom_statusmsg.appendChild(dom.createTextNode(u"Lisättiin uusi resepti!"))
	except Exception, e:
		success = False
		fill_form(data, dom)
		dom_errmsg = domutil.getContentElementById("error_text", dom)
		dom_errmsg.appendChild(dom.createTextNode(u"Virhe lisättäessä! (virhe: %s)" % str(e)))
	return success

def update(recipe_id, req, dom, con):
	"""Päivitä recipe_id:tä vastaava resepti tietokantaan req:n lomakkeen tiedoilla.

	Myös syötteet tarkistetaan ja lomakkeen virheilmoitukset asetetaan.

	Returns:
		True, jos onnistui.
	"""
	success = True
	data = get_form_data(req)
	errors = validate_form(data)
	if errors != None:
		fill_form(data, dom)
		fill_errors(errors, dom)
		return
	try:
		db_update(recipe_id, data, con)
		dom_statusmsg = domutil.getContentElementById("status_text", dom)
		dom_statusmsg.appendChild(dom.createTextNode(u"Päivitettiin resepti!"))
	except Exception, e:
		success = False
		fill_form(data, dom)
		dom_errmsg = domutil.getContentElementById("error_text", dom)
		dom_errmsg.appendChild(dom.createTextNode(u"Virhe muokatessa! (virhe: %s)" % str(e)))
	return success

def fetch(recipe_id, dom, con):
	"""Hae recipe_id:tä vastaava resepti tietokannasta ja aseta sen tiedot sivun lomakkeelle.

	Returns:
		True, jos onnistui.
	"""
	try:
		data = db_fetch(recipe_id, con)
	except Exception, e:
		dom_errmsg = domutil.getContentElementById("error_text", dom)
		dom_errmsg.appendChild(dom.createTextNode(u"Ongelmia tietokannasta haettaessa! (virhe: %s)" % str(e)))
		return False
	fill_form(data, dom)
	return True

def index(req):
	"""Näytä sivu, jolla voi lisätä reseptin. Jos pyynnössä on mukana "id"-kenttä, näytetään muokkaussivu vastaavalle reseptille."""
	global addpath
	global editpath

	vaiheita = 10

	recipe_id = req.form.getfirst("id")
	get_present = recipe_id != None
	submit_present = req.form.getfirst("submit") != None
	inserting = submit_present and not get_present
	updating = submit_present and get_present
	fetching = not (inserting or updating) and get_present

	if fetching or updating:
		htmlpath = editpath
	else:
		htmlpath = addpath
	dom = minidom.parse(htmlpath)

	con = dbutil.get_con(dbutil.dbfile)
	cur = con.cursor()

	# Luo dynaamiset dom-elementit.

	# Dropdown, jossa saatavilla olevat ruokalajit.
	dom_lajilista = domutil.getContentElementById("laji", dom)
	cur.execute("""
		select nimi, ruokalajiid as id
		from ruokalaji;
	""")
	for row in cur:
		dom_option = dom.createElement("option")
		dom_lajilista.appendChild(dom_option)
		dom_option.setAttribute("value", str(row["id"]).decode("utf-8"))
		dom_option.appendChild(dom.createTextNode(row["nimi"].decode("utf-8")))

	# Tekstikentät, joihin voi kirjoittaa reseptin vaiheet.
	dom_vaihelista = domutil.getContentElementById("vaiheet", dom)
	for i in xrange(0, vaiheita):
		dom_li = dom.createElement("li")
		dom_vaihelista.appendChild(dom_li)
		dom_textarea = dom.createElement("textarea")
		dom_li.appendChild(dom_textarea)
		dom_textarea.setAttribute("name", "teksti")
		dom_textarea.setAttribute("cols", "60")
		dom_textarea.setAttribute("rows", "2")

	to_index = False
	if inserting:
		insert(req, dom, con)
	elif updating:
		to_index = update(recipe_id, req, dom, con)
	elif fetching:
		fetch(recipe_id, dom, con)

	con.close()

	# Jos muokattiin olemassaolevaa reseptiä, ei jäädä lomakkeelle.
	if to_index:
		modutil.redirect(req, "index.py")

	req.content_type = "application/xhtml+xml; charset=utf-8"

	html = dom.toxml("utf-8")
	return html

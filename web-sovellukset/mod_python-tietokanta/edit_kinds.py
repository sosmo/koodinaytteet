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
htmlpath = os.path.join(templatepath, "manage_kinds.xhtml")
itempath = os.path.join(templatepath, "kind.xhtml")


def get_form_data(req):
	"""Hae pyynnöstä lomakkeen data.

	Returns:
		Data dict-muodossa. Avaimet: nimi (str), kuvaus (str)
	"""
	data = {}
	data["nimi"] = req.form.getfirst("nimi", "").strip()
	data["kuvaus"] = req.form.getfirst("kuvaus", "")
	return data

def validate_form(data):
	"""Validoi datan tiedot.

	Args:
		data: Data dict-muodossa. Avaimet: nimi (str), kuvaus (str)

	Returns:
		Jos kaikki ok, None. Muuten dictionary, jossa dataa vastaavissa avaimissa virheviestit.
	"""
	data_ok = True
	errors = {"nimi": "", "kuvaus": ""}

	if data["nimi"] == "":
		data_ok = False
		errors["nimi"] = u"Anna jokin nimi!"

	if not data_ok:
		return errors
	return None

def fill_form(id, data, dom):
	"""Hae pyynnöstä lomakkeen data.

	Returns:
		Data dict-muodossa. Avaimet: nimi (str), kuvaus (str)
	"""
	dom_item = domutil.getContentElementById("item%s" % id, dom)
	domutil.getElementById("nimi%s" % id, dom_item).setAttribute("value", data["nimi"].decode("utf-8"))
	domutil.getElementById("kuvaus%s" % id, dom_item).setAttribute("value", data["kuvaus"].decode("utf-8"))

def fill_errors(id, errors, dom):
	"""Täytä errors-dictin avaimien nimiä vastaavat DOM-elementit avaimia vastaavalla virheilmoituksella.

	Args:
		errors: Virheilmoitukset dict-muodossa. Avaimet: nimi (str), kuvaus (str), maara (str), laji (str), tekstit (str[]). HUOM oletetaan, että löytyy virhe-elementit muotoa avain+"_err"
	"""
	dom_item = domutil.getContentElementById("item%s" % id, dom)
	for error in errors.keys():
		errname = "%s_err%s" % (error, id)
		dom_err = domutil.getElementById(errname, dom_item)
		dom_err.appendChild(dom.createTextNode(errors[error]))

def index(req):
	"""Näytä lomake, jossa voi muokata kaikkia ruokalajeja yksi kerrallaan."""
	global htmlpath
	global itempath

	inserting = req.form.getfirst("insert") != None
	updating = req.form.getfirst("update") != None
	fetching = not (inserting or updating)

	dom = minidom.parse(htmlpath)
	itemdom = minidom.parse(itempath)
	dom_item_template = itemdom.documentElement

	con = dbutil.get_con(dbutil.dbfile)
	cur = con.cursor()


	# Hoida uusien tietojen päivittäminen tietokantaan ensiksi.
	errors = None
	db_error = False
	# Yleinen näytettävä viesti tietokantaoperaation onnistumisesta.
	status = u""
	if inserting or updating:
		data = get_form_data(req)
		errors = validate_form(data)
	if errors == None and inserting:
		try:
			cur.execute("begin")
			cmd = """
				insert into ruokalaji (nimi, kuvaus)
				values (?, ?);
			"""
			cur.execute(cmd, (data["nimi"], data["kuvaus"]))
			cur.execute("commit")
			status = u"Lisättiin uusi ruokalaji!"
		except Exception, e:
			cur.execute("rollback")
			db_error = True
			status = u"Virhe lisättäessä! (virhe: %s)" % str(e)
	elif errors == None and updating:
		id = req.form.getfirst("id")
		try:
			cur.execute("begin")
			cmd = """
				update ruokalaji
				set nimi = ?, kuvaus = ?
				where ruokalajiid = ?;
			"""
			cur.execute(cmd, (data["nimi"], data["kuvaus"], id))
			cur.execute("commit")
			status = u"Muutokset tallennettu!"
		except Exception, e:
			cur.execute("rollback")
			db_error = True
			status = u"Virhe tallentaessa! (virhe: %s)" % str(e)


	# Luo kentät jokaiselle tietokannasta löytyvälle ruokalajille, ja aseta niiden id:t ruokalajia vastaaviksi
	dom_items = domutil.getContentElementById("items", dom)
	cur.execute("""
		select nimi as nimi, kuvaus as kuvaus, ruokalajiid as id
		from ruokalaji;
	""")
	kinds = cur.fetchall()
	# Luo oma lomake jokaiselle ruokalajille.
	for row in kinds:
		id = str(row["id"])
		dom_item = dom_item_template.cloneNode(True)
		# Aseta piilotetun id-inputin arvo ruokalajiid:n mukaan
		dom_id = domutil.getElementById("id", dom_item)
		dom_id.setAttribute("value", id)
		# Aseta näille uniikit id:t ruokalajiid:n perusteella että löydetään myöhemmin eikä tule id-konflikteja
		elem_names = ["item", "id", "nimi", "kuvaus", "status_text", "nimi_err", "kuvaus_err"]
		for elem_name in elem_names:
			elem = domutil.getElementById(elem_name, dom_item)
			elem_id = elem.getAttribute("id")
			elem.setAttribute("id", elem_id+id)
		dom_items.appendChild(dom_item)
		# Aseta kenttien arvot tietokannan datan mukaan
		item_data = {}
		item_data["nimi"] = row["nimi"]
		item_data["kuvaus"] = row["kuvaus"]
		fill_form(id, item_data, dom)

	con.close()


	# Palauta syötetty data ja näytä virheviestit, jos syöte oli sopimatonta tai tapahtui tietokantavirhe.
	if inserting:
		if errors != None:
			fill_form("_new", data, dom)
			fill_errors("_new", errors, dom)
		dom_msg = domutil.getContentElementById("status_text_new", dom)
		dom_msg.appendChild(dom.createTextNode(status))
	elif updating:
		id = req.form.getfirst("id")
		if errors != None:
			fill_form(id, data, dom)
			fill_errors(id, errors, dom)
		dom_msg = domutil.getContentElementById("status_text%s" % id, dom)
		dom_msg.appendChild(dom.createTextNode(status))


	req.content_type = "application/xhtml+xml; charset=utf-8"

	html = dom.toxml("utf-8")
	return html

# -*- coding: utf-8 -*-

from mod_python import apache, Session, util as modutil
import simplejson as json
import os
import sys

resurssit = "/nashome3/saelosmo/html/tiea218/teht5"
sys.path.append(resurssit)
import db_utils as dbutil


def append_dict(dest, src):
	"""Lisää row:n avaimet/sisällöt dictionaryyn.
	"""
	for key in src.keys():
		dest[key] = src[key]
	return dest

def index(req):
	"""Hae reseptit aakkosjärjestyksessä."""
	con = dbutil.get_con(dbutil.dbfile)
	cur = con.cursor()

	accept_id = req.form.getfirst("id")
	if accept_id != None:
		accept_id = int(accept_id)
		cmd = """
				select distinct re.nimi as nimi, re.kuvaus as kuvaus, re.henkilomaara as maara, re.reseptiid as id, ru.nimi as laji, ru.ruokalajiid as lajiid
				from resepti re, ruokalaji ru
				where re.reseptiid = ?
					and ru.ruokalajiid = re.ruokalajiid
				order by re.nimi, re.reseptiid collate nocase;
			"""
		cur.execute(cmd, (accept_id,));
	else:
		cmd = """
				select distinct re.nimi as nimi, re.kuvaus as kuvaus, re.henkilomaara as maara, re.reseptiid as id, ru.nimi as laji, ru.ruokalajiid as lajiid
				from resepti re, ruokalaji ru
				where ru.ruokalajiid = re.ruokalajiid
				order by re.nimi, re.reseptiid collate nocase;
			"""
		cur.execute(cmd);


	aineet_cmd = """
			select ai.nimi as nimi, ai.aineid as aineid, yk.lyhenne as yksikko, yk.nimi as yksikkonimi, li.maara as maara
			from liittyy li, aine ai, yksikko yk
			where li.resepti_reseptiid = ?
				and ai.aineid = li.aine_aineid
				and yk.lyhenne = li.yksikko_lyhenne;
		"""
	vaiheet_cmd = """
			select vaihenro as numero, ohjeteksti as teksti
			from ohje
			where ohje.reseptiid = ?;
		"""

	reseptilista = []
	rows = cur.fetchall()
	for row in rows:
		resepti = append_dict({}, row)
		id = resepti["id"]

		cur.execute(aineet_cmd, (id,))
		aineet = []
		for i in cur:
			aine = append_dict({}, i)
			aineet.append(aine)
		resepti["aineet"] = aineet

		cur.execute(vaiheet_cmd, (id,))
		vaiheet = []
		for i in cur:
			vaihe = append_dict({}, i)
			vaiheet.append(vaihe)
		resepti["vaiheet"] = vaiheet

		reseptilista.append(resepti)

	con.close()

	req.content_type = "text; charset=utf-8"

	return json.dumps(reseptilista)

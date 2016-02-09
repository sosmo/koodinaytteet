# -*- coding: utf-8 -*-

from mod_python import apache, Session, util as modutil
import simplejson as json
import os
import sys

resurssit = "/nashome3/saelosmo/html/tiea218/teht5"
sys.path.append(resurssit)
import db_utils as dbutil


def index(req):
	"""Hae reseptit aakkosjärjestyksessä. Jos lomakkeessa on mukana "id", haetaan vain id:tä vastaava resepti."""
	con = dbutil.get_con(dbutil.dbfile)
	cur = con.cursor()

	accept_id = req.form.getfirst("id")
	if accept_id != None:
		accept_id = int(accept_id)

	cmd = """
		select re.nimi as nimi, re.reseptiid as id, re.kuvaus as kuvaus, re.henkilomaara as maara, re.ruokalajiid as lajiid, oh.ohjeteksti as teksti
		from resepti re
		left outer join ohje oh
		on re.reseptiid = oh.reseptiid
		order by re.nimi, re.reseptiid collate nocase;
	"""
	cur.execute(cmd)

	reseptilista = []
	resepti = None
	prev_row = None
	for row in cur:
		id = row["id"]
		if accept_id != None and id != accept_id:
			continue
		curr_row = id
		if prev_row != curr_row:
			resepti = {}
			resepti["nimi"] = row["nimi"]
			resepti["id"] = row["id"]
			resepti["kuvaus"] = row["kuvaus"]
			resepti["maara"] = row["maara"]
			resepti["laji"] = row["lajiid"]
			resepti["ohjetekstit"] = []
			reseptilista.append(resepti)
			prev_row = curr_row
		if row["teksti"] != None:
			resepti["ohjetekstit"].append(row["teksti"])

	con.close()

	req.content_type = "text; charset=utf-8"

	reseptijson = json.dumps(reseptilista)
	return reseptijson

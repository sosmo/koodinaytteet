# -*- coding: utf-8 -*-

from mod_python import apache, Session, util as modutil
import simplejson as json
import os
import sys

resurssit = "/nashome3/saelosmo/html/tiea218/teht5"
sys.path.append(resurssit)
import db_utils as dbutil


def index(req):
	con = dbutil.get_con(dbutil.dbfile)
	cur = con.cursor()

	cur.execute("""
		select nimi, kuvaus, ruokalajiid as id
		from ruokalaji
		order by nimi collate nocase;
	""")

	lajit = []
	for row in cur:
		laji = {}
		laji["nimi"] = row["nimi"]
		laji["id"] = row["id"]
		laji["kuvaus"] = row["kuvaus"]
		lajit.append(laji)

	req.content_type = "text; charset=utf-8"

	return json.dumps(lajit)

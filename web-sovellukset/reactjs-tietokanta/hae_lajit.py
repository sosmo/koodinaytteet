# -*- coding: utf-8 -*-

from mod_python import apache, Session, util as modutil
import simplejson as json
import os
import sys

resurssit = "/nashome3/saelosmo/html/tiea218/teht5"
sys.path.append(resurssit)
import db_utils as dbutil
import listaus


def index(req):
	con = dbutil.get_con(dbutil.dbfile)
	cur = con.cursor()

	cur.execute("""
		select nimi as nimi, kuvaus as kuvaus, ruokalajiid as id
		from ruokalaji
		order by nimi collate nocase;
	""")

	lajit = []
	for row in cur:
		laji = listaus.append_dict({}, row)
		lajit.append(laji)

	req.content_type = "text; charset=utf-8"

	return json.dumps(lajit)

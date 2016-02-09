# -*- coding: utf-8 -*-

from mod_python import apache, Session, util as modutil
import os
import sys

resurssit = "/nashome3/saelosmo/html/tiea218/teht5"
sys.path.append(resurssit)
import db_utils as dbutil
import tarkista_laji


def index(req):
	req.content_type = "text; charset=utf-8"

	data = tarkista_laji.get_data(req)

	if not tarkista_laji.data_ok(data):
		return "Virhe syötetyissä tiedoissa!"

	con = dbutil.get_con(dbutil.dbfile)
	cur = con.cursor()

	try:
		cur.execute("begin")
		cmd = """
			insert into ruokalaji (nimi, kuvaus)
			values(?, ?);
		"""
		cur.execute(cmd, (data["nimi"], data["kuvaus"]))

		cur.execute("commit")
	except Exception, e:
		cur.execute("rollback")
		return "Tietokantavirhe! (%s)" % str(e)

	con.close()

	return ""

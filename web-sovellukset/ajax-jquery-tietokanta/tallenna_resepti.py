# -*- coding: utf-8 -*-

from mod_python import apache, Session, util as modutil
import os
import sys

resurssit = "/nashome3/saelosmo/html/tiea218/teht5"
sys.path.append(resurssit)
import db_utils as dbutil
import tarkista_resepti


def index(req):
	"""Päivitä id:tä vastaava resepti."""
	req.content_type = "text; charset=utf-8"

	data = tarkista_resepti.get_data(req)

	if not tarkista_resepti.data_ok(data):
		return "Virhe syötetyissä tiedoissa!"

	recipe_id = int(req.form.getfirst("id"))

	con = dbutil.get_con(dbutil.dbfile)
	cur = con.cursor()

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
		return "Tietokantavirhe! (%s)" % str(e)

	con.close()

	return ""

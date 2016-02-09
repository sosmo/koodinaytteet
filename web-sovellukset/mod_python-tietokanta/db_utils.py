# -*- coding: utf-8 -*-

import sha
from pysqlite2 import dbapi2 as sqlite
from mod_python import apache, Session, util as modutil
import os
import sys

moduulit = "/nashome3/saelosmo/html/hidden/moduulit"
sys.path.append(moduulit)
import salt

dbpath = "/nashome3/saelosmo/html/hidden/kanta/"
pwfile = "passwords"
dbfile = "resepti"


def hash(username, password, salt=salt.salt):
	"""Luo tiiviste käyttäjän kirjautumistiedoista.

	salt-parametrin voi asettaa, jos haluaa käyttää jotain muuta kuin vakiota.

	Returns:
		Tiivisteen heksaesitystä vastaava merkkijono.
	"""
	hasher = sha.new()
	hasher.update(password.encode("utf-8"))
	hasher.update(username.encode("utf-8"))
	hasher.update(salt.encode("utf-8"))
	return hasher.hexdigest()

def get_con(dbname):
	"""Kysy yhteys halutun nimiseen tietokantaan ja aseta yhteydelle sopivat oletukset"""
	global dbpath

	con = sqlite.connect(os.path.join(dbpath, dbname))
	con.isolation_level = None
	con.text_factory = str
	con.row_factory = sqlite.Row
	return con

def verify(username, password, salt=salt.salt):
	"""Tarkista käyttäjän kirjautumistiedot.

	salt-parametrin voi asettaa, jos haluaa käyttää jotain muuta kuin vakiota.

	Returns:
		0, jos löytyi kelvollinen käyttäjä. 1, jos löytyi käyttäjätunnus mutta salasana väärin. 2, jos ei löytynyt tunnusta.
	"""
	global pwfile

	con = get_con(pwfile)
	cur = con.cursor()
	cur.execute("""
		select salasana
		from salasanat
		where tunnus like ?
	""", (username,))
	rows = cur.fetchall()
	con.close()
	if len(rows) < 1:
		return 2
	base_hash = rows[0]["salasana"]
	new_hash = hash(username, password, salt)
	if new_hash != base_hash:
		return 1
	return 0

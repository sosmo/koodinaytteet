# -*- coding: utf-8 -*-

import sha
from pysqlite2 import dbapi2 as sqlite
from mod_python import apache, Session, util as modutil
import os
import sys

dbpath = "/nashome3/saelosmo/html/hidden/kanta/"
dbfile = "resepti"


def get_con(dbname):
	"""Kysy yhteys halutun nimiseen tietokantaan ja aseta yhteydelle sopivat oletukset"""
	global dbpath

	con = sqlite.connect(os.path.join(dbpath, dbname))
	con.isolation_level = None
	con.text_factory = str
	con.row_factory = sqlite.Row
	return con

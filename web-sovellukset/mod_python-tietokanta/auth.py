# -*- coding: utf-8 -*-

from mod_python import apache, Session, util as modutil
import os
import sys

moduulit = "/nashome3/saelosmo/html/hidden/moduulit"
resurssit = "/nashome3/saelosmo/html/tiea218/teht4"
sys.path.append(moduulit)
sys.path.append(resurssit)
import salt
import db_utils as dbutil

loginpath = os.path.join(resurssit, "login.py")


def handler(req):
	"""Ohjaa kirjautumattomat käyttäjät aina kirjautumissivulle."""
	# Päästetään suoraan eteenpäin jos kirjauduttu.
	if req.session["kirjautunut"] == "1":
		return apache.OK

	# Jos kirjautumatta, päästetään vain login-sivulle. ps varo rekursiivisia uudelleenohjauksia
	if req.filename != loginpath:
		modutil.redirect(req, "login.py")

	return apache.OK

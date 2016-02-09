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

htmlpath = os.path.join(os.path.join(resurssit, "templates"), "login.xhtml")


def index(req):
	"""Näytä kirjautumislomake tai tarkista ohjaa etusivulle jos löytyy kelvolliset kirjautumistiedot."""
	dom = minidom.parse(htmlpath)

	# Jos login-lomakkeen tiedot pyynnössä mukana, tehdään tarkistukset.
	if req.form.getfirst("submit") != None:
		tunnus = req.form.getfirst("tunnus", "")
		salasana = req.form.getfirst("salasana", "")
		virhe = dbutil.verify(tunnus, salasana)
		# Tallennetaan kirjautuminen ja ohjataan etusivulle jos tiedot ok.
		if virhe == 0:
			req.session["kirjautunut"] = "1"
			req.session.save()
			modutil.redirect(req, "index.py")
		# Ilmoitetaan virheistä, jos tiedot väärin.
		if virhe == 1:
			msg = u"Virheellinen salasana!"
		else:
			msg = u"Käyttäjätunnusta ei löydy!"
		dom_errmsg = domutil.getContentElementById("error_text", dom)
		dom_p = dom.createElement("p")
		dom_errmsg.appendChild(dom_p)
		dom_p.appendChild(dom.createTextNode(msg))

	req.content_type = "application/xhtml+xml; charset=utf-8"

	html = dom.toxml("utf-8")
	return html

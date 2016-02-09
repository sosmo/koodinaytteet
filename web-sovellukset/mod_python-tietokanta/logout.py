# -*- coding: utf-8 -*-

from mod_python import apache, Session, util as modutil
from xml.dom import minidom, Node
import os
import sys

resurssit = "/nashome3/saelosmo/html/tiea218/teht4"
sys.path.append(resurssit)
import dom_utils as domutil

htmlpath = os.path.join(os.path.join(resurssit, "templates"), "logout.xhtml")


def index(req):
	"""Näytä viestisivu ja kirjaa käyttäjä ulos."""
	req.content_type = "application/xhtml+xml; charset=utf-8"

	dom = minidom.parse(htmlpath)

	if hasattr(req, "session"):
		session = req.session
		session.invalidate()

	html = dom.toxml("utf-8")
	return html

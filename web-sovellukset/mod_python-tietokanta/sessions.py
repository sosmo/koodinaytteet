# -*- coding: utf-8 -*-

from mod_python import apache, Session

def handler(req):
	"""Varmista, että pyynnöstä löytyy aina istunto. Lisää istunnon oletusominaisuudet."""
	if not hasattr(req, "session"):
		req.session = Session.Session(req)

		if req.session.is_new():
			req.session["kirjautunut"] = "0"

	return apache.OK

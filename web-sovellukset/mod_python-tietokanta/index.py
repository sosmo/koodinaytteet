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

htmlpath = os.path.join(os.path.join(resurssit, "templates"), "index.xhtml")


def index(req):
	"""Näytä listaus tietokannassa olevista resepteistä. Reseptiä klikkaamalla pääsee muokkaussivulle."""
	req.content_type = "application/xhtml+xml; charset=utf-8"

	dom = minidom.parse(htmlpath)

	dom_reseptit = domutil.getContentElementById("reseptit", dom)
	dom_reseptilista = dom.createElement("ul")
	dom_reseptit.appendChild(dom_reseptilista)

	con = dbutil.get_con(dbutil.dbfile)
	cur = con.cursor()

	cmd = """
		select re.nimi as nimi, re.reseptiid as id, oh.ohjeteksti as teksti
		from resepti re
		left outer join ohje oh
		on re.reseptiid = oh.reseptiid
		order by re.nimi, re.reseptiid collate nocase;
	"""
	cur.execute(cmd)

	# Lisää reseptien vaiheet sivulle listana. Jos vaihe on reseptin ensimmäinen, luodaan uusi reseptin nimellä otsikoitu taso.
	prev_row = (None, None)
	for row in cur:
		curr_row = (row["nimi"], row["id"])
		if prev_row != curr_row:
			dom_outer_li = dom.createElement("li")
			dom_reseptilista.appendChild(dom_outer_li)
			dom_h = dom.createElement("h2")
			dom_outer_li.appendChild(dom_h)
			dom_a = dom.createElement("a")
			dom_h.appendChild(dom_a)
			# Otsikoissa on linkit sivulle, jossa reseptiä pääsee muokkaamaan.
			dom_a.setAttribute("href", "add_recipe.py?id=%s" % (row["id"],))
			dom_a.appendChild(dom.createTextNode(row["nimi"].decode("utf-8")))
			dom_ol = dom.createElement("ol")
			dom_outer_li.appendChild(dom_ol)
			prev_row = curr_row
		if row["teksti"] != None:
			dom_li = dom.createElement("li")
			dom_ol.appendChild(dom_li)
			dom_li.appendChild(dom.createTextNode(row["teksti"].decode("utf-8")))
		# tyhjät listat ei kelpaa
		else:
			tmp = dom_ol.parentNode
			tmp.removeChild(dom_ol)

	con.close()

	html = dom.toxml("utf-8")
	return html

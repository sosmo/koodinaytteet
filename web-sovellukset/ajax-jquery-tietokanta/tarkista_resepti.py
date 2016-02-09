# -*- coding: utf-8 -*-

from mod_python import apache, Session, util as modutil
import simplejson as json
import os
import sys

resurssit = "/nashome3/saelosmo/html/tiea218/teht5"
sys.path.append(resurssit)
import db_utils as dbutil


def validoi_data(data):
	"""Validoi get_datan paluuarvoa vastaavan dictionaryn kentät. Palauta uusi dict, jossa virheilmoitukset"""
	virheet = {}
	for key in data.keys():
		virheet[key] = ""
	if "nimi" in data:
		if data["nimi"] == "":
			virheet["nimi"] = "Anna jokin nimi!"
	if "maara" in data:
		try:
			lkm = int(data["maara"])
			if lkm < 1:
				virheet["maara"] = "Henkilömäärän pitäisi olla ainakin 1!"
		except:
			virheet["maara"] = "Anna numero henkilömääräksi!"
	return virheet

def data_ok(data):
	"""Palauta True, jos get_datan paluuarvoa vastaavan dictionaryn kentät ovat ok"""
	virheet = validoi_data(data)
	for key in virheet.keys():
		if virheet[key] != "":
			return False
	return True

def get_data(req):
	"""Palauta lomakkeen tiedot dictionaryna. Avaimet: nimi, kuvaus, maara, laji, tekstit."""
	data = {}
	data["nimi"] = req.form.getfirst("nimi").strip()
	data["kuvaus"] = req.form.getfirst("kuvaus")
	data["maara"] = req.form.getfirst("maara").strip()
	data["laji"] = req.form.getfirst("laji")
	data["tekstit"] = req.form.getlist("teksti")
	return data

# Jos tulee vielä tarvetta tarkistaa kenttä kerrallaan
#def get_partial_data(req):
	#data = {}
	#if "nimi" in req.form:
		#data["nimi"] = req.form.getfirst("nimi").strip()
	#if "kuvaus" in req.form:
		#data["kuvaus"] = req.form.getfirst("kuvaus")
	#if "maara" in req.form:
		#data["maara"] = req.form.getfirst("maara").strip()
	#if "laji" in req.form:
		#data["laji"] = req.form.getfirst("laji")
	#if "teksti" in req.form:
		#data["tekstit"] = req.form.getlist("teksti")
	#return data

def index(req):
	"""Tarkista lomakkeesta löytyvät reseptin tiedot.

	Form fieldit, jotka tarkistetaan: nimi ja maara. Vastaavat tietokannasta löytyviä reseptin sarakkeita.

	Returns:
		JSON-objekti, jossa avaimina tarkistettavaksi annetut kentät, ja arvoina niitä vastaavat virheviestit tai tyhjä jono jos ei virhettä.
	"""
	req.content_type = "text; charset=utf-8"

	data = get_data(req)
	virheet = validoi_data(data)
	return json.dumps(virheet)

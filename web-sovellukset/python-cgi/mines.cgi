#!/usr/bin/env python
# -*- coding: utf-8 -*-

# aiheuttaa ylimääräisen rivin dokumentin alkuun, xml ei hyväksy sellaista
#print "Content-Type: text/html\n\n"
print "Content-Type: text/html\n"

import cgi
import random
import simplejson as json
import time
import cgitb
import urllib
from xml.dom import minidom
from xml.dom import Node
import xml.dom

cgitb.enable()

# kuvien urlit
miina = "images/mine.svg"
# HTML-pohja
template = "index.html"

# Ruutujen määrä pysty- ja vaakasuunnassa
size = -1
# Otsikon teksti
text = None
# Laudalla olevien miinojen kokonaismäärä
mine_count = 1
# Lautaa vastaava taulukko, jossa on eri ruutujen sisällöt. -1 vastaa pommia, 0 avaamatonta ruutua, 1 avattua ruutua.
square_contents = None

dom = None
# Pelilautataulukko
table = None;
# Piilokenttien vanhempi
hidden_inputs = None

form = cgi.FieldStorage()

def clearChildren(node):
    """Poista noden lapsinodet.
    """
    child = node.firstChild
    while child != None:
        node.removeChild(child)
        child = node.firstChild

def setText(element, text):
    """Aseta elementin teksti ja poista muut lapset.
    """
    global dom

    clearChildren(element)
    element.appendChild(dom.createTextNode(text))

def getElementById(topElement, id):
    """Hae id:tä vastaava elementti alkaen topElementistä. None, jos ei löydy.
    """
    if topElement.nodeType != Node.ELEMENT_NODE:
        return None
    if topElement.getAttribute("id") == id:
        return topElement
    if len(topElement.childNodes) == 0:
        return None
    for child in topElement.childNodes:
        child_result = getElementById(child, id)
        if child_result != None:
            return child_result

def getContentElementById(id):
    """Hae tämän dokumentin body-elementistä alkaen id:tä vastaavia.
    """
    global dom

    top = dom.getElementsByTagName("body")[0]
    return getElementById(top, id)


def update_heading():
    """Aseta text-globaalin mukaan sivun otsikko. Tyhjää ei aseteta.
    """
    global text

    if text.strip() == "":
        return
    heading = getContentElementById("heading")
    setText(heading, text)

def setup_board():
    """Luo sizen kokoinen table-elementti sivulle
    """
    global table

    wrapper = getContentElementById("table_wrapper")
    clearChildren(wrapper)

    table = dom.createElement("table")
    table.setAttribute("class", "mine_table")
    for i in xrange(0, size):
        tr = dom.createElement("tr")
        for j in xrange(0, size):
            td = dom.createElement("td")
            tr.appendChild(td)
        table.appendChild(tr)

    wrapper.appendChild(table)

def create_mines(size, mine_count):
    """Luo taulukko, jossa on satunnaisissa paikoissa miinoja.

    Args:
        size (int): Taulukon koko.
        mine_count (int): Luotavien miinojen määrä.

    Returns:
        int[]: 2-ulotteinen taulukko, jossa -1 vastaa pommia, 0 avaamatonta ruutua, 1 avattua ruutua.
    """
    square_contents = [[0 for x in xrange(size)] for x in xrange(size)]

    # Luo jokaista ruutua vastaava indeksi ja aseta ne satunnaiseen järjestykseen.
    mine_indices = []
    for i in xrange(size*size):
        mine_indices.append(i)
    random.shuffle(mine_indices)

    # Muunna indeksit taulukon riveiksi ja sarakkeiksi ja aseta miinat niihin.
    for i in xrange(mine_count):
        row = int(mine_indices[i] / size)
        col = mine_indices[i] % size
        square_contents[row][col] = -1

    return square_contents

def setup_mines():
    """Aseta miina- ja ruutu-painikkeet miinataulukkoon square_contentsin sisällön perusteella. Talleta myös asetetut ruutujen tilat piilokenttiin tulevia CGI-kutsuja varten.

    Painikkeille asetetaan arvot JSON-muodossa [row, col], niin että kun tiettyä ruutua klikataan, saadaan CGI:n kautta klikattu ruutu ja sen arvosta ruudun sijainti.

    Ruutujen arvot on piilokentissä JSON-muodossa {row: int, col: int, content: int}
    """
    global size
    global dom
    global square_contents
    global hidden_inputs

    for i in xrange(0, size):
        for j in xrange(0, size):
            content = square_contents[i][j]
            td = get_square(i, j)
            if content == -1:
                child = create_mine()
            else:
                child = create_square()
            child.setAttribute("name", "square")
            child.setAttribute("value", json.dumps([i, j]))
            td.appendChild(child)

            input = dom.createElement("input")
            input.setAttribute("type", "hidden")
            input.setAttribute("name", "square_content")
            data = json.dumps({"row": i, "col": j, "content": content})
            input.setAttribute("value", data)
            hidden_inputs.appendChild(input)

def get_square_content(row, col):
    """Hae lomakkeen kenttiin tallennetuista ruutujen tiedoista riviä ja saraketta vastaavan ruudun sisällön kertova numero.

    Returns:
        int: Numero, joka kuuluu olla ruudussa paikassa [row][col].
    """
    global form

    for content in form.getlist("square_content"):
        data = json.loads(content)
        if data["row"] == row and data["col"] == col:
            return data["content"]
    return None

def update_mines(del_row, del_col):
    """Aseta miina- ja ruutu-painikkeet miinataulukkoon lomakkeesta saatujen kenttien perusteella. Talleta myös asetetut ruutujen tilat piilokenttiin tulevia CGI-kutsuja varten.

    Painikkeille asetetaan arvot JSON-muodossa [row, col], niin että kun tiettyä ruutua klikataan, saadaan CGI:n kautta klikattu ruutu ja sen arvosta ruudun sijainti.

    Ruutujen arvot on piilokentissä JSON-muodossa {row: int, col: int, content: int}

    Args:
        Ruutu, jonka sijainti vastaa muuttujia del_row ja del_col jätetään lisäämättä taulukkoon ja piilokenttiin.
    """
    global size
    global dom
    global square_contents
    global hidden_inputs

    for i in xrange(0, size):
        for j in xrange(0, size):
            if i == del_row and j == del_col:
                square_contents[i][j] = 1
            else:
                square_content = get_square_content(i, j)
                square_contents[i][j] = square_content

                # Ota uusi painike käyttöön vain jos ruutu on avaamaton
                if square_content != 1:
                    td = get_square(i, j)
                    if square_content == -1:
                        child = create_mine()
                    elif square_content == 0:
                        child = create_square()
                    child.setAttribute("name", "square")
                    child.setAttribute("value", json.dumps([i, j]))
                    td.appendChild(child)

            input = dom.createElement("input")
            input.setAttribute("type", "hidden")
            input.setAttribute("name", "square_content")
            data = json.dumps({"row": i, "col": j, "content": square_contents[i][j]})
            input.setAttribute("value", data)
            hidden_inputs.appendChild(input)

def create_mine():
    """Luo miinaa vastaava button
    """
    global dom
    global template

    input = dom.createElement("input")
    input.setAttribute("type", "submit")
    input.setAttribute("class", "mine")
    return input

def create_square():
    """Luo tyhjää ruutua vastaava button
    """
    global dom

    input = dom.createElement("input")
    input.setAttribute("type", "submit")
    input.setAttribute("class", "empty")
    return input

def get_square(row, col):
    """Hae table-elementistä solu paikasta [row][col].
    """
    global table

    tr = table.childNodes[row]
    return tr.childNodes[col]

def new_page():
    """Luo uusi sivu annetuilla tiedoilla.
    """
    global square_contents

    square_contents = create_mines(size, mine_count)

    update_heading()
    setup_board()
    setup_mines()

def update_page(del_row, del_col):
    """Päivitä nykyistä sivua poistamalla yksi ruutu.

    Args:
        Ruutu, jonka sijainti vastaa muuttujia del_row ja del_col poistetaan.
    """
    global square_contents

    square_contents = [[0 for x in xrange(size)] for x in xrange(size)]

    update_heading()
    setup_board()
    update_mines(del_row, del_col)

def get_clicked_square():
    """Ota klikattu ruutu-nappula formista ja kaiva sen arvosta ruudun koordinaatit.
    """
    global form

    square = form.getfirst("square")
    if square == None:
        return None

    coords = json.loads(square)
    return (coords[0], coords[1])

def handle_cgi():
    """Luo uusi sivu tai päivitä CGI-infon perusteella.
    """
    global form

    global size
    global text
    global mine_count

    if "x" in form:
        size_raw = form.getfirst("x")
        try:
            size = int(size_raw)
        except:
            size = -1
    if "teksti" in form:
        text = form.getfirst("teksti")

    # Jos kokoa ei ole saatu mistään niin lopetetaan
    if size < 6 or size > 12:
        return False

    # Puuttuva teksti siedetään
    if text == None:
        text = ""

    sizebox = getContentElementById("x")
    sizebox.setAttribute("value", str(size))
    textbox = getContentElementById("teksti")
    textbox.setAttribute("value", text.decode("utf-8"))

    mine_count = size*size / 3

    # Jos "luo"-painike löytyy, lähetettiin lomake sitä painamalla
    # en voi käsittää miksi tämä on aina false
    #if "main_submit" in form:
    if form.getfirst("main_submit") != None:
        new_page()
    else:
        clicked = get_clicked_square()
        # Klikattiin ruutua
        if clicked != None:
            update_page(*clicked)
        # Tultiin urlilla
        else:
            new_page()

    return True

def main():
    """Tulosta HTML miinaharavataulukolle.
    """
    global form
    global dom
    global hidden_inputs

    dom = minidom.parse(template)
    hidden_inputs = getContentElementById("hidden_inputs")

    success = handle_cgi()
    if not success:
        p = dom.createElement("p")
        setText(p, "Anna kelvollinen koko")
        getContentElementById("content_wrapper").appendChild(p)

    html = dom.toxml("utf-8")

    print html
    #print html.encode("utf-8", "ignore")

if __name__ == '__main__':
    main()

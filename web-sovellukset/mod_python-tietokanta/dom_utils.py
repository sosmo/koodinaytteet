# -*- coding: utf-8 -*-

from xml.dom import minidom, Node

def clearChildren(node):
    """Poista noden lapsinodet.
    """
    child = node.firstChild
    while child != None:
        node.removeChild(child)
        child = node.firstChild

def setText(element, text, dom):
    """Aseta elementin teksti ja poista muut lapset.
    """
    clearChildren(element)
    element.appendChild(dom.createTextNode(text))

def getElementById(id, topElement):
    """Hae id:tä vastaava elementti alkaen topElementistä. None, jos ei löydy.
    """
    if topElement.nodeType != Node.ELEMENT_NODE:
        return None
    if topElement.getAttribute("id") == id:
        return topElement
    if len(topElement.childNodes) == 0:
        return None
    for child in topElement.childNodes:
        child_result = getElementById(id, child)
        if child_result != None:
            return child_result

def getContentElementById(id, dom):
    """Hae dokumentin body-elementistä alkaen id:tä vastaavia.
    """
    top = dom.getElementsByTagName("body")[0]
    return getElementById(id, top)

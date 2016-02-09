"use strict";

// int; Ruutujen määrä pysty- ja vaakasuunnassa.
var squareCount = 1;
// int; Laudalla olevien miinojen kokonaismäärä.
var mineCount = 1;
// int[][]; Lautaa vastaava taulukko, jossa on eri ruutujen sisällöt. -1 vastaa pommia, 0 tai suurempi vastaa ruutua, jonka lähellä on vastaava määrä pommeja.
var squareContents = null;
// int[][]; Lautaa vastaava taulukko, jossa on eri ruutujen tilat. 0 vastaa koskematonta ruutua, 1 vastaa avattua ruutua, -1 vastaa merkittyä ruutua.
var squareStates = null;
// int; Jäljellä oleva merkitsemättömien miinojen määrä.
var minesLeft = 1;
// int; Jäljellä oleva avaamattomien miinattomien ruutujen määrä.
var safeSquaresLeft = 1;

// node; Pelilautataulukko.
var table = null;
// node; Taulukon alue.
var tableWrapper = null;
// node; Peliin liittyvä tilanneviesti.
var statusMsg = null;

/// Main. Alusta HTML, tyylit ja globaalit eventit, aloita uusi peli.
window.onload = function() {
	createStyles();

	var instr = createInstructions();
	document.body.appendChild(instr);
	var form = createForm();
	document.body.appendChild(form);

	statusMsg = createStatusMsg();
	document.body.appendChild(statusMsg);

	tableWrapper = createTableWrapper();
	document.body.appendChild(tableWrapper);

	newGame();

	window.addEventListener("resize", function(event) { resizeTable(table); }, false);
}

/// Luo CSS-luokka dokumenttiin. http://stackoverflow.com/questions/1720320/how-to-dynamically-create-css-class-in-javascript-and-apply
/// @name string; Luokan nimi ja selectorit yms.
/// @rules string; Luokan sisältämät säännöt, ilman aaltosulkuja.
function createClass(name, rules){
	var style = document.createElement("style");
	style.type = "text/css";
	document.getElementsByTagName("head")[0].appendChild(style);
	if(!(style.sheet||{}).insertRule)
		(style.styleSheet || style.sheet).addRule(name, rules);
	else
		style.sheet.insertRule(name+"{"+rules+"}",0);
}

/// Aseta tyyli elementille. http://stackoverflow.com/questions/1720320/how-to-dynamically-create-css-class-in-javascript-and-apply
/// @name string; Luokka, joka asetetaan.
/// @element string; Elementti, jolle luokka asetetaan.
/// @doRemove bool; Poista luokka asettamisen sijaan.
function applyClass(name, element, doRemove){
	if(typeof element.valueOf() === "string"){
		element = document.getElementById(element);
	}
	if(!element) return;
	if(doRemove){
		element.className = element.className.replace(new RegExp("\\b" + name + "\\b","g"));
	}else{
		element.className = element.className + " " + name;
	}
}

/// Luo dokumenttiin tarvittavat tyylit.
function createStyles() {
	var html = "margin: 0em;" +
			"padding: 0em;" +
			"height: 100%";
	createClass("html", html);
	var body = "box-sizing: border-box;" +
			"display: flex;" +
			"flex-flow: column nowrap;" +
			"margin: 0em;" +
			"padding: 1em;" +
			"height: 100%"; +
			"width: 100%";
	createClass("body", body);
	var bodyChildren = "flex: 0 0 auto;";
	createClass("body > *", bodyChildren);
	var table = "border: 1px solid black;" +
			"border-spacing: 0;" +
			"height: 100%;" +
			"width: 100%;" +
			"border-collapse: collapse;";
	createClass("#mine_table", table);
	var tr = "margin: 0;" +
			"padding: 0;";
	createClass("#mine_table tr", tr);
	var td = "margin: 0;" +
			"padding: 0;" +
			"text-align:center;" +
			"vertical-align: top;" +
			"background-color: white;";
	createClass("#mine_table td", td);
	var span = "border: 4px groove silver;" +
			"box-sizing: border-box;" +
			"min-width: 47px;" +
			"min-height: 47px;" +
			"height: 100%;" +
			"width: 100%;" +
			"margin: 0;" +
			"padding: 0;" +
			"background-color: silver;" +
			"color: green;" +
			"display: block;";
	createClass("#mine_table td > span", span);
	var img = "width: 100%;" +
			"height: 100%;" +
			"padding: 0px;" +
			"margin: 0px;" +
			"display: block;";
	createClass("#mine_table img", img);
}

/// Luo ohje-div.
function createInstructions() {
	var instructions = document.createElement("p");
	var instrText = "Kerro luotavan ruudukon koko. Ruudukko on yhtä monta ruutua leveä kuin korkea."
	instructions.appendChild(document.createTextNode(instrText));
	return instructions;
}

/// Luo lomake, josta löytyy laudan koolle ja miinamäärälle inputit.
function createForm() {
	var form = document.createElement("form");
	form.setAttribute("action", "");
	form.setAttribute("method", "post");
	var fieldset = document.createElement("fieldset");

	var par1 = document.createElement("p");
	var label1 = document.createElement("label");
	var label1Text = "Leveys ";
	label1.appendChild(document.createTextNode(label1Text));
	var textInput1 = document.createElement("input");
	textInput1.setAttribute("id", "input_width");
	textInput1.setAttribute("type", "text");
	textInput1.setAttribute("name", "width");
	textInput1.setAttribute("value", "8");
	label1.appendChild(textInput1);
	par1.appendChild(label1);
	fieldset.appendChild(par1);

	var par2 = document.createElement("p");
	var label2 = document.createElement("label");
	var label2Text = "Pommit ";
	label2.appendChild(document.createTextNode(label2Text));
	var textInput2 = document.createElement("input");
	textInput2.setAttribute("id", "input_count");
	textInput2.setAttribute("type", "text");
	textInput2.setAttribute("name", "bomb_count");
	textInput2.setAttribute("value", "4");
	label2.appendChild(textInput2);
	par2.appendChild(label2);
	fieldset.appendChild(par2);

	form.appendChild(fieldset);

	var submit = document.createElement("input");
	submit.setAttribute("type", "submit");
	submit.setAttribute("value", "Luo");
	submit.style.margin = "1em 0em";
	submit.addEventListener("click", function(event) {
		event.preventDefault();
		newGame();
	}, false);
	form.appendChild(submit);

	return form;
}

/// Luo status-div.
function createStatusMsg() {
	var div = document.createElement("div");
	div.setAttribute("id", "status");
	return div;
}

/// Luo wrapper-div pelitaulukolle.
function createTableWrapper() {
	var tableWrapper = document.createElement("div");
	tableWrapper.setAttribute("id", "table_wrapper");
	tableWrapper.style.flex = "1 1 auto";
	return tableWrapper;
}

/// Parsi kentät taulukon koosta ja miinamäärästä ja talleta globaaleihin muuttujiin. Värjää kentät punaisiksi jos virhe.
/// @return bool; true, jos kelvollisten lukujen parsinta onnistui.
function handleForm() {
	var inputSize = document.getElementById("input_width");
	var inputCount = document.getElementById("input_count");
	if (!inputSize || !inputCount) {
		return false;
	}
	var size = parseInt(inputSize.value);
	// min-raja 8, max 32
	if (isNaN(size) || size  < 8 || size > 32) {
		inputSize.style.backgroundColor = "red";
		return false;
	}
	var count = parseInt(inputCount.value);
	// Ei sallita, jos pommeja on yhtä paljon kuin ruutuja tai enemmän.
	if (isNaN(count) || count < 0 || count > size*size-1) {
		inputCount.style.backgroundColor = "red";
		return false;
	}
	inputSize.style.backgroundColor = "white";
	inputCount.style.backgroundColor = "white";

	squareCount = size;
	mineCount = count;

	minesLeft = mineCount;
	safeSquaresLeft = squareCount*squareCount - minesLeft;

	updateMinesStatus();

	return true;
}

/// Luo pelitaulukko. Soluissa olevat span-elementit saavat id:n "span"+n, jossa n kasvaa vasemmalta alas edettäessä.
/// @squareCount int; Vaaka/pystyruutujen määrä.
/// @return node; Luotu taulukko.
function createMineTable(squareCount) {
	var table = document.createElement("table");
	table.setAttribute("id", "mine_table");
	var accum = 0;
	for (var i = 0; i < squareCount; i++) {
		var row = document.createElement("tr");
		for (var j = 0; j < squareCount; j++) {
			var td = document.createElement("td");
			var span = document.createElement("span");
			span.id = "span" + accum++;
			span.addEventListener("click", spanClickHandler, false);
			span.addEventListener("contextmenu", spanRightClickHandler, false);
			td.appendChild(span);
			row.appendChild(td);
		}
		table.appendChild(row);
	}

	return table;
}

/// Luo uusi pelitaulukko ja alusta uusi peli.
function newGame() {
	if (!handleForm()) {
		return;
	}

	table = createMineTable(squareCount);
	while (tableWrapper.hasChildNodes()) {
		tableWrapper.removeChild(tableWrapper.lastChild);
	}
	tableWrapper.appendChild(table);

	resizeTable(table);

	squareContents = null;
	squareStates = null;
}

/// Hae haluttu solu taulukosta.
/// @table node; Haluttu taulukko.
/// @return node; Solu kysytystä paikasta.
function getTableCell(table, row, col) {
	var trow = table.childNodes[row];
	return trow.childNodes[col];
}

/// Suorita jokaisella taulukon solulla funktio.
/// @table node; Haluttu taulukko.
/// @func (node,int,int)->; Funktio, joka ottaa parametrikseen td-elementin, ja sitä vastaavan x- ja y-koordinaatin. Kutsutaan kerran jokaista td-elementtiä kohden.
function iterateTableCells(table, func) {
	var rows = table.childNodes;
	for (var i = 0; i < squareCount; i++) {
		var cells = rows[i].childNodes;
		for (var j = 0; j < squareCount; j++) {
			func(cells[j], i, j);
		}
	}
}

/// Sovita taulukon koko tableWrapperin kokoon niin, että leveys ja pituus pysyvät samoina.
function resizeTable() {
	var wrapperStyle = window.getComputedStyle(tableWrapper);
	// Flexboxin käyttö korkeuden hakemiseen ei toimi kunnolla kuin Firefoxissa
	//var height = parseInt(wrapperStyle.height.replace("px", ""));
	var body = document.getElementsByTagName("body")[0];
	var bodyStyle = window.getComputedStyle(body);
	var bodyHeight = parseInt(bodyStyle.height.replace("px", ""));
	var bodyPadding = parseInt(bodyStyle.paddingBottom.replace("px", ""));
	var tableTop = table.offsetTop;
	var height = bodyHeight - tableTop - bodyPadding;
	height = Math.max(0, height);
	var width = parseInt(wrapperStyle.width.replace("px", ""));
	var size = Math.min(width, height);
	table.style.width = size + "px";
	table.style.height = size + "px";

	var cellSize = size / squareCount;
	iterateTableCells(table, function(cell, row, col) {
		cell.style.width = cellSize + "px";
		cell.style.height = cellSize + "px";
		cell.style.fontSize = (cellSize / 1.5) + "px";
		cell.style.lineHeight = (cellSize / 1.2) + "px";
	});
}

/// Sekoita taulukon arvojen järjestys.
/// @array int[]; Sotkettava taulukko.
function shuffle(array) {
	for (var i = 0; i < array.length; i++) {
		var iOther = Math.floor(Math.random() * array.length);
		var temp = array[i];
		array[i] = array[iOther];
		array[iOther] = temp;
	}
}

/// Alusta uusi halutun kokoinen taulukko nollilla. http://stackoverflow.com/questions/3689903/how-to-create-a-2d-array-of-zeroes-in-javascript
/// @dimensions int[]; Ulottuvuuksien koot ulommaisin ensimmäisenä.
/// @return int[]; 0-taulukko, jossa kysytty määrä ulottuvuuksia.
function zeros(dimensions) {
	var array = [];

	for (var i = 0; i < dimensions[0]; ++i) {
		array.push(dimensions.length === 1 ? 0 : zeros(dimensions.slice(1)));
	}

	return array;
}

/// Luo taulukko, jossa on lukuja, jotka vastaavat peliruutujen sisältöä.
/// @size int; Taulukon korkeus/leveys.
/// @mineCount int; Taulukkoon menevien miinojen määrä. Miinojen paikat ovat satunnaisia.
/// @rowForbidden int; Rivi, jonka ilmoittamaan ruutuun ei saa miinoja pistää.
/// @colForbidden int; Sarake, jonka ilmoittamaan ruutuun ei saa miinoja pistää.
/// @return int[]; 2-ulotteinen taulukko, jossa -1 vastaa pommia, 0 tai suurempi vastaa ruutua, jonka lähellä on vastaava määrä pommeja.
function setupMines(size, mineCount, rowForbidden, colForbidden) {
	squareContents = zeros([size, size]);

	// Luo jokaista ruutua vastaava indeksi ja aseta ne satunnaiseen järjestykseen.
	var mineIndices = [];
	for (var i = 0; i < size*size; i++) {
		mineIndices[i] = i;
	}
	shuffle(mineIndices);

	// Ota haluttu määrä satunnaisia indeksejä kaikkien indeksien joukosta. Tarkka rivi ja sarake lasketaan indekseistä jakamalla.
	var upperLimit = mineCount;
	for (var i = 0; i < upperLimit; i++) {
		var row = Math.floor(mineIndices[i] / size);
		var col = mineIndices[i] % size;
		// Ohita kielletty ruutu
		if (row === rowForbidden && col === colForbidden) {
			upperLimit++;
		}
		else {
			squareContents[row][col] = -1;
		}
	}

	// Kasvata miinoitettujen ruutujen ympäristöjen arvoja.
	for (var i = 0; i < size; i++) {
		for (var j = 0; j < size; j++) {
			if (squareContents[i][j] === -1) {
				mapSurroundingCells(squareContents, i, j, function(x) {
					if (x < 0) {
						return x;
					}
					return x + 1;
				});
			}
		}
	}
}

/// Korvaa taulukon solut funktion arvoilla.
/// @table int[]; Haluttu 2-ulotteinen taulukko.
/// @func (int)->int; Funktio, joka ottaa parametrikseen kokonaisluvun ja palauttaa kokonaisluvun. Jokainen taulukon alkio korvataan tuloksella, jonka funktio palauttaa alkiolla.
function mapSurroundingCells(table, row, col, func) {
	for (var i = row-1; i <= row+1; i++) {
		for (var j = col-1; j <= col+1; j++) {
			if (i < 0 || j < 0 || i >= table.length || j >= table.length || (i === row && j === col)) {
				continue;
			}
			table[i][j] = func(table[i][j]);
		}
	}
}

/// Aseta ruutujen sisällöt (miinat/numerot) aiemmin luotuun table-nodeen.
function setupBoard() {
	for (var i = 0; i < squareCount; i++) {
		for (var j = 0; j < squareCount; j++) {
			var square = squareContents[i][j];
			var cell = getTableCell(table, i, j);
			var span = cell.firstChild;
			if (square === -1) {
				span.appendChild(createMine());
			}
			else {
				var textSpan = document.createElement("span");
				textSpan.style.display = "none";
				textSpan.style.visibility = "visible";
				var num = square;
				// Nollaa ei merkitä numerolla.
				if (num === 0) {
					num = "";
				}
				textSpan.appendChild(document.createTextNode(num));
				span.appendChild(textSpan);
			}
		}
	}
}

/// Luo näkymätön miinakuva.
function createMine() {
	var mine = document.createElement("img");
	mine.setAttribute("src", "mine.svg");
	mine.style.display = "none";
	mine.style.visibility = "visible";
	return mine;
}

/// "Avaa" ruutu, eli näytä sen span-elementin lapset ja piilota span. Laajentaa miinattomat ruudut ympäriltä jos avattiin tyhjä. Ajaa loseGame jos ruudussa oli miina, winGame jos kaikki ruudut on avattu.
function exposeSquare(row, col) {
	// Avattuja ei käsitellä.
	if (squareStates[row][col] === 1) {
		return;
	}

	var cell = getTableCell(table, row, col);
	var span = cell.firstChild;
	removeSpan(span);

	squareStates[row][col] = 1;

	if (squareContents[row][col] >= 0) {
		safeSquaresLeft--;
		if (safeSquaresLeft < 1) {
			winGame();
			return;
		}
	}
	else if (squareContents[row][col] === -1) {
		cell.style.background = "red";
		loseGame();
		return;
	}

	// Jos 0, avataan rekursiivisesti kaikkien nollien ympärysruudut.
	if (squareContents[row][col] === 0) {
		for (var i = row-1; i <= row+1; i++) {
			for (var j = col-1; j <= col+1; j++) {
				if (i < 0 || j < 0 || i >= squareCount || j >= squareCount) {
					continue;
				}
				exposeSquare(i, j);
			}
		}
	}
}

/// Piilota span ja näytä lapsi.
function removeSpan(span) {
	span.style.visibility = "hidden";
	var child = span.firstChild;
	child.style.display = "block";
}

/// Näytä ruudut ja päivitä peliviesti, kun hävitään.
function loseGame() {
	iterateTableCells(table, function(cell, row, col) {
		var span = cell.firstChild;
		removeSpan(span);
		// Värjää ruutu, jos miinaton ruutu oli merkitty.
		if (squareStates[row][col] === -1 && squareContents[row][col] !== -1) {
			cell.style.backgroundColor = "#ff9955";
		}
	});
	setText(statusMsg, "Hävisit pelin");
}

/// Näytä ruudut ja päivitä peliviesti, kun voitetaan.
function winGame() {
	iterateTableCells(table, function(cell, row, col) {
		var span = cell.firstChild;
		removeSpan(span);
	});
	setText(statusMsg, "Voitit pelin!");
}

/// Olio, joka vastaa koordinaattia.
function Coordinate(x, y) {
	this.x = x;
	this.y = y;
}

/// Hae pelitaulukon span-elementin sijainti sen nimen perusteella.
function getSpanCoords(span) {
	var spanName = span.getAttribute("id");
	var spanNumber = parseInt(spanName.replace("span", ""));
	var row = Math.floor(spanNumber / squareCount);
	var col = spanNumber % squareCount;
	return new Coordinate(row, col);
}

/// Asettele miinat jos eka klikkaus (klikatussa ei voi olla miinaa). Näytä ruudun sisältö.
function spanClickHandler(event) {
	event.preventDefault();

	var target = event.target;
	if (target.tagName !== "SPAN") {
		return;
	}
	var coords = getSpanCoords(target);
	var row = coords.x;
	var col = coords.y;

	if (!squareStates) {
		squareStates = zeros([squareCount, squareCount]);
	}
	if (!squareContents) {
		setupMines(squareCount, mineCount, row, col);
		setupBoard();
	}

	// Älä avaa jos merkitty.
	if (squareStates[row][col] === -1) {
		return;
	}

	exposeSquare(row, col);
}

/// Merkitse miina.
function spanRightClickHandler(event) {
	event.preventDefault();

	var target = event.target;
	if (target.tagName !== "SPAN") {
		return;
	}
	var coords = getSpanCoords(target);
	var row = coords.x;
	var col = coords.y;

	if (!squareStates) {
		squareStates = zeros([squareCount, squareCount]);
	}

	// Koskematon
	if (squareStates[row][col] === 0) {
		squareStates[row][col] = -1;
		minesLeft--;

		updateMinesStatus();
		target.style.backgroundColor = "#ff9955";
	}
	// Merkitty
	else if (squareStates[row][col] === -1) {
		squareStates[row][col] = 0;
		minesLeft++;

		updateMinesStatus();
		// TODO: sama kuin tyylissä määritelty, ei kauhean kiva
		target.style.backgroundColor = "silver";
	}
}

/// Aseta statukseksi miinojen määrä
function updateMinesStatus() {
	setText(statusMsg, "Miinoja jäljellä: " + minesLeft);
}

/// Aseta noden teksti.
function setText(node, text) {
	while (node.firstChild !== null) {
		node.removeChild(node.firstChild);
	}
	node.appendChild(document.createTextNode(text));
}

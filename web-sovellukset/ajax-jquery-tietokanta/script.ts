/// <reference path="jquery.d.ts" />
/// <reference path="jquery.form.d.ts" />

"use strict";


interface ReseptiData {
	nimi: string;
	id: number;
	ohjetekstit: string[];
	kuvaus?: string;
	laji?: string;
	maara?: string;
}

interface LajiData {
	nimi: string;
	id: number;
	kuvaus?: string;
}



var reseptiLomakeAlustettu = false;
var lajiLomakeAlustettu = false;


window.addEventListener("load", main, false);

function main(event) {
	naytaReseptit();

	$("#nav_kaikki").on("click", function(event) {
			event.preventDefault();
			naytaReseptit();
		});
	$("#nav_lisaa").on("click", function(event) {
			event.preventDefault();
			naytaReseptinLisays();
		});
	$("#nav_lajit").on("click", function(event) {
			event.preventDefault();
			naytaLajit();
		});
}

// Näytä yleinen virheilmoitus, kun AJAX epäonnistuu.
function ajaxVirhe(request, status, errorText) {
	//$("#yleinen_virhe").text(request.responseText+"\n"+errorText);
	$("#yleinen_virhe").text("JavaScript-virhe! Tarkista, että selain on ajan tasalla ja JavaScript sallittuna.");
}

// Valitse näytettävä sisältö id:n perusteella.
function naytaSisalto(id: string) {
	$("#content > *").addClass("hidden");
	$("#"+id).removeClass("hidden");
}



// Näytä lista resepteistä.
function naytaReseptit() {
	naytaSisalto("listing_templ");

	// Hae ja listaa reseptit
	$.ajax({
			async: true,
			url: "listaus.py",
			data: "",
			processData: false,
			dataType: "json",
			type: "post",
			success: listaaReseptit,
			error: ajaxVirhe
		});
}

// Täytä reseptilista tietokannan tiedoilla.
// @data: Taulukko, jossa tiedot jokaista tietokannan reseptiä kohden.
function listaaReseptit(data: ReseptiData[], status, request) {
	var $reseptilista = $("#reseptilista");
	$reseptilista.empty();
	var avaaResepti = function(event) {
		event.preventDefault();
		var $a = $(event.target);
		var reseptinId: number = $a.data("id");
		naytaReseptinMuokkaus(reseptinId);
	};
	for (var i = 0; i < data.length; i++) {
		var resepti: ReseptiData = data[i];
		var $ulompiLi = $("<li>");
		$reseptilista.append($ulompiLi);
		var $h2 = $("<h2>");
		$ulompiLi.append($h2);
		var $a = $("<a>");
		$h2.append($a);
		$a.attr("href", "#");
		$a.text(resepti["nimi"]);
		$a.data("id", resepti["id"]);
		$a.on("click", avaaResepti);
		var $ohjeLista = $("<ol>");
		$ulompiLi.append($ohjeLista);
		var ohjeet: string[] = resepti["ohjetekstit"];
		for (var j = 0; j < ohjeet.length; j++) {
			var ohje: string = ohjeet[j];
			var $ohje = $("<li>");
			$ohjeLista.append($ohje);
			$ohje.text(ohje);
		}
	}
}



// Tee valmistelut reseptilomakkeelle kun se avataan ekaa kertaa.
function alustaReseptiLomake() {
	var $vaiheet = $("#resepti_vaiheet");
	for (var i = 0; i < 10; i++) {
		var $li = $("<li>");
		$vaiheet.append($li);
		var $textarea = $("<textarea>");
		$li.append($textarea);
		$textarea.attr("name", "teksti");
		$textarea.attr("cols", "60");
		$textarea.attr("rows", "2");
	}

	// Käsittele syöte reseptilomakkeen kenttään ja kutsu lopuksi callback-funktiota lomakkeen virheilmoitukset sisältävä objekti parametrina.
	var handleInput = function(callback: (virheet: {[i:string]: string})=>void) {
		var tmp = function(data, status, request) {
			handlaaReseptiVirheet(data);
			callback(data);
		};
		tarkistaResepti(tmp);
	};

	$("#resepti_nimi").on(
		"input",
		function(event) {
			handleInput(function(data) {
				$("#resepti_nimi_err").text(data["nimi"]);
			});
		});
	$("#resepti_maara").on(
		"input",
		function(event) {
			handleInput(function(data) {
				$("#resepti_maara_err").text(data["maara"]);
			});
		});

	reseptiLomakeAlustettu = true;
}

// Tarkista reseptilomakkeen tiedot ja kutsu lopuksi callback-funktiota saadulla eri kenttiä vastaavat virheilmoitukset sisältävällä objektilla.
function tarkistaResepti(callback) {
	var form = $("#lisayslomake").formSerialize();
	$.ajax({
			async: true,
			url: "tarkista_resepti.py",
			data: form,
			processData: false,
			dataType: "json",
			type: "post",
			success: callback,
			error: ajaxVirhe
		});
}

// Ota käyttöön reseptilomakkeen submit-painike jos syötteet on ok.
function handlaaReseptiVirheet(virheet: {[i:string]: string}) {
	var $nappi = $("#submit_recipe");
	var keys = Object.keys(virheet);
	for (var i = 0; i < keys.length; i++) {
		if (virheet[keys[i]] != "") {
			$nappi.prop("disabled", true);
			return;
		}
	}
	$nappi.prop("disabled", false);
}

function tyhjennaReseptiLomake() {
	$("#lisayslomake").clearForm();

	$("#recipe_status_text").text("");
	$("#resepti_nimi_err").text("");
	$("#resepti_kuvaus_err").text("");
	$("#resepti_maara_err").text("");
}

// Täytä reseptilomakkeen lajivalikko datan tiedoilla.
// @data: Taulukko, jossa tiedot oliona jokaista lajia kohti.
function taytaLajiValikko(data: LajiData[], status, request) {
	var $lajivalikko = $("#resepti_laji");
	$lajivalikko.empty();
	for (var i = 0; i < data.length; i++) {
		var laji: LajiData = data[i];
		var $option = $("<option>");
		$lajivalikko.append($option);
		$option.val(laji["id"].toString());
		$option.text(laji["nimi"]);
	}
	var tmp: any = $lajivalikko[0];
	tmp.selectedIndex = 0;
	//$lajivalikko.children().eq(0).prop("selected", true);
}


// Näytä tyhjä lomake uuden reseptin lisäystä varten.
function naytaReseptinLisays() {
	naytaSisalto("add_recipe_templ");

	$("#add_recipe_heading").text("Lisää uusi resepti");
	$("#submit_recipe").val("Lisää");
	tyhjennaReseptiLomake();

	if (!reseptiLomakeAlustettu) {
		alustaReseptiLomake();
	}

	// Hae lajit alasvetovalikkoon.
	$.ajax({
			async: true,
			url: "hae_lajit.py",
			data: "",
			processData: false,
			dataType: "json",
			type: "post",
			success: taytaLajiValikko,
			error: ajaxVirhe
		});

	$("#submit_recipe").off("click");
	$("#submit_recipe").on("click", lisaaResepti);
}

// Lisää lomakkeen resepti tietokantaan.
function lisaaResepti(event) {
	event.preventDefault();
	var form: string = $("#lisayslomake").formSerialize();
	$.ajax({
			async: true,
			url: "lisaa_resepti.py",
			data: form,
			processData: false,
			dataType: "text",
			type: "post",
			success: reseptiLisatty,
			error: ajaxVirhe
		});
}

// Näytä tilaviesti reseptin lisäyksen tai lisäysyrityksen jälkeen.
function reseptiLisatty(data: string, status, request) {
	var $status = $("#recipe_status_text");
	if (data == "") {
		tyhjennaReseptiLomake();
		$status.removeClass("errortext");
		$status.text("Lisättiin uusi resepti!");
	}
	else {
		$status.addClass("errortext");
		$status.text(data);
	}
}


// Näytä muokkausta varten lomake, jossa on id:tä vastaavan reseptin tiedot.
function naytaReseptinMuokkaus(reseptinId: number) {
	naytaSisalto("add_recipe_templ");

	$("#add_recipe_heading").text("Muokkaa reseptiä");
	$("#submit_recipe").val("Tallenna");
	$("#submit_recipe").prop("disabled", false);
	tyhjennaReseptiLomake();

	if (!reseptiLomakeAlustettu) {
		alustaReseptiLomake();
	}

	// Hae lajit alasvetovalikkoon.
	$.ajax({
			async: true,
			url: "hae_lajit.py",
			data: "",
			processData: false,
			dataType: "json",
			type: "post",
			success: taytaLajiValikko,
			error: ajaxVirhe
		});

	// Hae ja täytä reseptin tiedot lomakkeelle.
	$.ajax({
			async: true,
			url: "listaus.py",
			data: {"id": reseptinId.toString()},
			processData: true,
			dataType: "json",
			type: "post",
			success: naytaResepti,
			error: ajaxVirhe
		});

	$("#submit_recipe").off("click");
	$("#submit_recipe").on("click", function(event) {
			event.preventDefault();
			tallennaResepti(reseptinId);
		});
}

// Näytä lomakkeella reseptin tiedot.
// @data: Lista, josta otetaan eka alkio, jossa reseptin tiedot oliona.
function naytaResepti(data: ReseptiData[], status, request) {
	var resepti: ReseptiData = data[0];
	$("#resepti_nimi").val(resepti["nimi"]);
	$("#resepti_kuvaus").val(resepti["kuvaus"]);
	$("#resepti_maara").val(resepti["maara"].toString());
	$("#resepti_laji").val(resepti["laji"]);
	var ohjeet: string[] = resepti["ohjetekstit"];
	var $tekstiboxit = $("#resepti_vaiheet li > textarea");
	for (var i = 0; i < ohjeet.length; i++) {
		var ohje: string = ohjeet[i];
		$tekstiboxit.eq(i).val(ohje);
	}
}

// Päivitä lomakkeelle syötetyt tiedot id:tä vastaavaan reseptiin.
function tallennaResepti(reseptinId: number) {
	var form: string = $("#lisayslomake").formSerialize();
	form += "&id=" + reseptinId;
	$.ajax({
			async: true,
			url: "tallenna_resepti.py",
			data: form,
			processData: false,
			dataType: "text",
			type: "post",
			success: reseptiTallennettu,
			error: ajaxVirhe
		});
}

// Näytä tilaviesti reseptin tallennuksen tai yrityksen jälkeen.
function reseptiTallennettu(data: string, status, request) {
	var $status = $("#recipe_status_text");
	if (data == "") {
		$status.removeClass("errortext");
		$status.text("Resepti tallennettu!");
	}
	else {
		$status.addClass("errortext");
		$status.text(data);
	}
}



// Tee valmistelut lajilomakkeelle kun se avataan ekaa kertaa.
function alustaLajiLomake() {
	$("#uusi_laji input[name='nimi']").on("input", handlaaLajiInput);
	$("#uusi_laji input[name='insert']").on("click", lisaaLaji);
	lajiLomakeAlustettu = true;
}

// Näytä lista tietokannasta löytyvistä lajeista.
function naytaLajit() {
	naytaSisalto("edit_kinds_templ");

	$("#uusi_laji_lomake").clearForm();
	$("#uusi_laji_status").text("");

	if (!lajiLomakeAlustettu) {
		alustaLajiLomake();
	}

	$.ajax({
			async: true,
			url: "hae_lajit.py",
			data: "",
			processData: false,
			dataType: "json",
			type: "post",
			success: listaaLajit,
			error: ajaxVirhe
		});
}

function listaaLajit(data: LajiData[], status, request) {
	var $lajit = $("#lajit");
	$lajit.empty();
	for (var i = 0; i < data.length; i++) {
		var laji: LajiData = data[i];
		var $div = $("#laji_template").clone();
		$div.attr("id", "");
		$lajit.append($div);
		$div.find("input[name='nimi']").val(laji["nimi"]);
		$div.find("input[name='kuvaus']").val(laji["kuvaus"]);
		$div.find("input[name='nimi']").on("input", handlaaLajiInput);
		var $nappi = $div.find("input[name='update']");
		$nappi.data("id", laji["id"]);
		$nappi.on("click", tallennaLaji);
	}
}

// Käsittele syötteet lajin kenttiin. Ota vastaavan lomakkeen lähetysnappi käyttöön ja näytä mahdolliset kenttään tehdyt virheet kentän vieressä.
function handlaaLajiInput(event) {
	var field = event.target;
	var $field = $(field);
	tarkistaLaji(
		field,
		function(data, status, request) {
			handlaaLajiVirheet(field, data);
			// Hae virhekenttä kentän läheltä ja näytä siinä vastaava datasta löytyvä virheilmoitus.
			$field.parent().siblings(".errortext").text(data[$field.attr("name")]);
		});
}

// Tarkista lajilomakkeen tiedot ja kutsu lopuksi callback-funktiota saadulla eri kenttiä vastaavat virheilmoitukset sisältävällä objektilla. Käsiteltävä lajilomake valitaan etsimällä lähin form-elementti srcFieldin vanhemmista.
function tarkistaLaji(srcField, callback: (data: any, status, request)=>void) {
	var $form = $(srcField).closest("form");
	var form: string = $form.formSerialize();
	$.ajax({
			async: true,
			url: "tarkista_laji.py",
			data: form,
			processData: false,
			dataType: "json",
			type: "post",
			success: callback,
			error: ajaxVirhe
		});
}

// Ota lajilomakkeen lähetysnappi käyttöön jos syötteet ok.
function handlaaLajiVirheet(srcField, virheet) {
	var $nappi = $(srcField).closest("form").find("input[name='update'], input[name='insert']");
	var keys = Object.keys(virheet);
	for (var i = 0; i < keys.length; i++) {
		if (virheet[keys[i]] != "") {
			$nappi.prop("disabled", true);
			return;
		}
	}
	$nappi.prop("disabled", false);
}

function tyhjennaLajiStatukset() {
	$("#lajit span[data-type='laji_status']").text("");
	$("#uusi_laji_status").text("");
}


// Hae eventin aloittanutta nappulaa vastaava form, ja päivitä sitä vastaava laji lomakkeen tiedoilla.
function tallennaLaji(event) {
	event.preventDefault();
	var $nappi = $(event.target);
	var id = $nappi.data("id");
	var form: string = $nappi.closest("form").formSerialize();
	form += "&id=" + id;
	$.ajax({
			async: true,
			url: "tallenna_laji.py",
			data: form,
			processData: false,
			dataType: "text",
			type: "post",
			success: function(data, status, request) {
					lajiTallennettu(event.target, data, status, request);
				},
			error: ajaxVirhe
		});
}

// Näytä tilaviesti lajin tallennuksen tai yrityksen jälkeen, pyyhi aiemmat tilaviestit.
function lajiTallennettu(field, data: string, status, request) {
	tyhjennaLajiStatukset();
	var $status = $(field).siblings("span");
	if (data == "") {
		$status.removeClass("errortext");
		$status.text("Laji tallennettu!");
	}
	else {
		$status.addClass("errortext");
		$status.text(data);
	}
}


// Lisää uusi laji lomakkeen tiedoilla.
function lisaaLaji(event) {
	event.preventDefault();
	var $nappi = $("#uusi_laji_nappi");
	var form: string = $("#uusi_laji_lomake").formSerialize();
	$.ajax({
			async: true,
			url: "lisaa_laji.py",
			data: form,
			processData: false,
			dataType: "text",
			type: "post",
			success: lajiLisatty,
			error: ajaxVirhe
		});
}

// Näytä tilaviesti lajin tallennuksen tai yrityksen jälkeen, pyyhi aiemmat tilaviestit.
function lajiLisatty(data: string, status, request) {
	tyhjennaLajiStatukset();
	var $status = $("#uusi_laji_status");
	if (data == "") {
		$("#uusi_laji_lomake").clearForm();
		naytaLajit();
		$status.removeClass("errortext");
		$status.text("Lisättiin uusi laji!");
	}
	else {
		$status.addClass("errortext");
		$status.text(data);
	}
}

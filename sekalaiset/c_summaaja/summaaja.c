#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <errno.h>
#include <limits.h>

// Parsii kokonaisluvun jonosta
//
// str: parsittava jono
// kohde: luku, johon parsinnan tulos laitetaan
// end: pointteri parsitun luvun jälkeen tulevaan merkkiin
// return: 1 jos parsintavirhe, 2 jos ylivuoto, muuten 0
int oma_stoi(char* str, long* kohde, char** end) {
	char* p = str;
	errno = 0;
	long l = strtol(str, &p, 0);
	// Näin käy jos parsintavirhe
	if (p == str) {
		return 1;
	}
	// Näin käy jos ylivuoto
	if ((l == LONG_MIN || l == LONG_MAX) && errno == ERANGE) {
		return 2;
	}
	*kohde = l;
	*end = p;
	return 0;
}

// Kopioi jono toiseen ilman kiellettyjä merkkejä
//
// str: käsiteltävä jono
// kohde: tänne käsittelyn tulos
// f: jos palauttaa true merkille, niin merkki poistetaan
void poista_merkit(const char* str, char* kohde, int (*f)(char)) {
	const char* p = str;
	size_t i = 0;
	for (p; *p != 0; p++) {
		if (f(*p)) {
			kohde[i++] = *p;
		}
	}
	kohde[i] = 0;
}

int ei_space(char c) {
	char kielletty[] = {' ', '\t', '\n', '\r', '\b', '\f', '\a'};
	size_t n = sizeof(kielletty) / sizeof(*kielletty);
	size_t i = 0;
	for (i; i < n; i++) {
		if (c == kielletty[i]) {
			return 0;
		}
	}
	return 1;
}

// Parsi yhteenlaskun tulos
//
// str: parsittava jono
// tulos: luku, johon parsinnan tulos laitetaan
// return: 1 jos parsintavirhe, 2 jos ylivuoto, muuten 0
int summaa(char* str, long* tulos) {
	char karsittu[strlen(str)];
	poista_merkit(str, karsittu, ei_space);

	long kertyma = 0;
	int err;
	long luku;
	char* p = karsittu;
	while (*p != 0) {
		err = oma_stoi(p, &luku, &p);
		if (err != 0) {
			return err;
		}
		if (sum(&kertyma, luku) != 0) {
			return 2;
		}
	}
	*tulos = kertyma;
	return 0;
}

int sum(long* eka, long toka) {
	if (*eka > 0 && toka > 0 && toka > LONG_MAX - *eka) {
		return 1;
	}
	else if (*eka < 0 && toka < 0 && toka < LONG_MIN - *eka) {
		return 1;
	}
	*eka += toka;
	return 0;
}

// Parsi yhteenlasku tiedostosta
//
// f: tiedoston numero
// tulos: luku, johon parsinnan tulos laitetaan
// return: 1 jos parsintavirhe, 2 jos ylivuoto, 3 jos tiedostovirhe, muuten 0
int summaa_tiedostosta(const int f, long* tulos) {
	size_t KOKO = 50;
	char str[KOKO];
	size_t count = read(f, &str, KOKO-1);
	if (count < 0) {
		return 3;
	}

	// nullit pois...
	size_t nulleja = 0;
	size_t i = 0;
	size_t j = 0;
	for (i; i < count; i++) {
		if (str[i] != 0) {
			str[j++] = str[i];
		}
		else {
			nulleja++;
		}
	}
	count -= nulleja;

	str[count] = 0;
	return summaa(str, tulos);
}

int main() {
	long tulos = 0;
	int err = summaa_tiedostosta(0, &tulos);
	if (err == 0) {
		printf("%d\n", tulos);
	}
	else if (err == 1) {
		fprintf(stderr, "Parse error\n");
	}
	else if (err == 2) {
		fprintf(stderr, "Arithmetic error\n");
	}
	else {
		fprintf(stderr, "Read error\n");
	}
	return err != 0;
}

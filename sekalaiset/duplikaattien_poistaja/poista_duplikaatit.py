import sys
import os
import string
import hashlib
import functools

"""
Näytä ja poista saman sisältöiset tiedostot parametrina annetusta kansiosta. Poisto mahdollista joko kerralla tai tiedostot yksitellen valiten.

TODO: Unicode-tuki tiedostonimissä
"""


def hash_file(f, hasher, buf_size=25600):
	""" Luo tiiviste tiedostosta.

	Args:
		f: Tiedosto (avoin tiedostokahva)
		hasher: Hash-funktio
		buf_size: Tavumäärä, joka tiedostosta luetaan kerralla

	Returns:
		Tiedoston tiiviste
	"""
	buffer = f.read(buf_size)
	while len(buffer) > 0:
		hasher.update(buffer)
		buffer = f.read(buf_size)
	return hasher.hexdigest()

def iterate_files(path, action):
	""" Käy tiedostot läpi ja suorita jokaiselle haluttu toiminto.

	Args:
		path: Operoitava polku (str)
		action: Polun jokaiselle lapsitiedostoille suoritettava toiminto (funktio, joka ottaa polun parametrina)
	"""
	dirs = [path]
	while dirs != []:
		curr_dir = dirs.pop(0)
		names = os.listdir(curr_dir)
		names.sort()
		for name in names:
			f_path = os.path.join(curr_dir, name)
			if os.path.isdir(name):
				dirs.append(f_path)
			else:
				action(f_path)

def main():
	dir = "."
	if len(sys.argv) > 1:
		dir = sys.argv[1].strip()
	os.chdir(dir)

	files = {}
	dup_groups = {}

	def group_files(f_path):
		if os.path.abspath(f_path) == sys.argv[0]:
			return
		try:
			file = open(f_path, "rb")
			file_hash = hash_file(file, hashlib.sha1())
			if not file_hash in files:
				files[file_hash] = f_path
			else:
				if not file_hash in dup_groups:
					dup_groups[file_hash] = [files[file_hash]]
				dup_groups[file_hash].append(f_path)
			file.close()
		except:
			pass

	iterate_files(".", group_files)

	if len(dup_groups) < 1:
		print("No duplicates!")
		input()
		return

	for group in dup_groups.values():
		padding = ""
		for path in group:
			print(padding.encode("utf-8") + path.encode("utf-8"))
			padding = "  -> "

	n_dups = sum([len(x) for x in dup_groups.values()])
	print("\nFound {} duplicates!\n".format(n_dups))
	accept = input(
			"Enter a to delete all but the \"header\" files\n" +
				"Enter m to select files to be deleted one by one\n" +
				"Cancel with anything else\n")

	if accept == "a":
		for group in dup_groups.values():
			os.remove(group[0])

	if accept == "m":
		print("Enter the number of the file you want to keep, anything else skips")
		for group in dup_groups.values():
			i = 1
			for path in group:
				print(str(i).encode("utf-8") + ". ".encode("utf-8") + path.encode("utf-8"))
				i += 1
			while True:
				ans = input("")
				try:
					marked = int(ans)
					if marked < 1 or marked > len(group):
						print("Invalid number!")
						continue
					i = 1
					for path in group:
						if i != marked:
							os.remove(path)
						i += 1
				except:
					pass
				break

	print("Done!")

if __name__ == '__main__':
	main()

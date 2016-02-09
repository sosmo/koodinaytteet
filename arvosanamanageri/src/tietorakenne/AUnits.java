package tietorakenne;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kentat.FN;
import kentat.Ref;
import sekalaiset.StorageException;

/**
 * Abstrakti luokka, joka sisältää samantyyppisiä datayksikköjä.
 * 
 * @author Sampo Osmonen
 * @param <T> Sisällytettävien yksikköjen tyyppi.
 */
public abstract class AUnits<T extends AUnit> implements IUnits<T> {

	private Class<T> type;
	private List<T> items = new ArrayList<>();
	private IdCounter idCounter = new IdCounter();
	private boolean muutettu = true;
	private String tiedostonimi;


	/**
	 * @param type Talletettavien yksikköjen tyyppi.
	 * @param tiedostonimi Nimi, jolla yksiköt tallennetaan tekstitiedostoon tallennettaessa.
	 */
	public AUnits(Class<T> type, String tiedostonimi) {
		this.type = type;
		this.tiedostonimi = tiedostonimi;
	}


	@Override
	public int getCount() {
		return this.items.size();
	}

	protected List<T> getItems() {
		return this.items;
	}

	@Override
	public void add(T t) {
		Ref[] refs = t.getRefFields();
		Comparable<?>[] data = t.getDataFields();
		IllegalStateException up = new IllegalStateException("Oliossa ei voi olla tyhjiä kenttiä lisätessä");
		for (Ref r : refs) {
			if (r == null) {
				throw up;
			}
		}
		for (Comparable<?> c : data) {
			if (c == null) {
				throw up;
			}
		}
		getItems().add(t);
		// Jos alkiolla ei ole vielä kelvollista id:tä, annetaan sille vuorossa oleva id.
		if (t.getRef() == null) {
			int id = idCounter.popId();
			((AUnit)t).setRef(new Ref(type, id));
		}
		// Jos alkiolla on jo id (esim. parsinnan seurauksena), joka on idCounterin nykyarvoa suurempi, korjataan idCounterin arvo.
		else if (t.getRef().getId() >= this.idCounter.getId()) {
			int id = t.getRef().getId() + 1;
			this.idCounter.setId(id);
		}
		this.muutettu = true;
	}

	@Override
	public T get(Ref ref) {
		for (T t : getItems()) {
			if (t.getRef().equals(ref)) {
				return t;
			}
		}
		return null;
	}

	@Override
	public List<T> getAll() {
		return new ArrayList<>(getItems());
	}

	@Override
	public T remove(Ref id) {
		List<T> alkiot = getItems();
		for (int i = 0; i < getCount(); i++) {
			if (alkiot.get(i).getRef().equals(id)) {
				this.muutettu = true;
				return alkiot.remove(i);
			}
		}
		return null;
	}

	@Override
	public List<T> getAll(Object data, FN field) {
		List<T> loytyneet = new ArrayList<>();
		for (T t : getItems()) {
			if (t.getField(field, Object.class).equals(data)) {
				loytyneet.add(t);
			}
		}
		return loytyneet;
	}

	@Override
	public List<T> getMatches(String haku, FN kentta) {
		String verrattava = haku.toLowerCase();
		List<T> loytyneet = new ArrayList<>();
		for (T t : getItems()) {
			String kentanData = t.getFieldAsString(kentta).toLowerCase();
			if (kentanData.contains(verrattava)) {
				loytyneet.add(t);
			}
		}
		return loytyneet;
	}

	public List<T> getMatches(String[] searchTerms, FN[] fields) {
		if (searchTerms.length != fields.length) {
			throw new IllegalArgumentException("Hakutermien ja kenttänimien määrien pitää vastata toisiaan");
		}
		List<T> matches = new ArrayList<>();
		for (T t : getItems()) {
			boolean match = true;
			for (int i = 0; i < searchTerms.length; i++) {
				if (searchTerms[i] == null || searchTerms[i].equals("")) {
					continue;
				}
				String target = searchTerms[i].toLowerCase();
				Object fieldData = t.getField(fields[i]);
				String fieldString = fieldData.toString();
				if (fieldData instanceof Ref) {
					if (!fieldString.equals(target)) {
						match = false;
						break;
					}
				}
				else if (!fieldString.contains(target)) {
					match = false;
					break;
				}
			}
			if (match) {
				matches.add(t);
			}
		}
		return matches;
	}

	public boolean removeAll(Object data, FN kentta) {
		boolean loytyi = false;
		Iterator<T> it = getItems().iterator();
		while (it.hasNext()) {
			T t = it.next();
			if (t.getField(kentta, Object.class).equals(data)) {
				it.remove();
				loytyi = true;
			}
		}
		if (loytyi) {
			this.muutettu = true;
		}
		return loytyi;
	}

	public boolean removeMatches(String haku, FN kentta) {
		String verrattava = haku.toLowerCase();
		boolean loytyi = false;
		Iterator<T> it = getItems().iterator();
		while (it.hasNext()) {
			T t = it.next();
			String kentanData = t.getFieldAsString(kentta).toLowerCase();
			if (kentanData.contains(verrattava)) {
				it.remove();
				loytyi = true;
			}
		}
		if (loytyi) {
			this.muutettu = true;
		}
		return loytyi;
	}

	public boolean removeMatches(String[] searchTerms, FN[] fields) {
		if (searchTerms.length != fields.length) {
			throw new IllegalArgumentException("Hakutermien ja kenttänimien määrien pitää vastata toisiaan");
		}
		boolean found = false;
		Iterator<T> it = getItems().iterator();
		while (it.hasNext()) {
			T t = it.next();
			for (int i = 0; i < searchTerms.length; i++) {
				String target;
				if (searchTerms[i] == null) {
					target = "";
				}
				else {
					target = searchTerms[i].toLowerCase();
				}
				String fieldData = t.getFieldAsString(fields[i]).toLowerCase();
				if (fieldData.contains(target)) {
					it.remove();
					found = true;
				}
			}
		}
		if (found) {
			this.muutettu = true;
		}
		return found;
	}

	protected T luoUusi() {
		T uusi = null;
		try {
			uusi = this.type.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e) {
			System.err.println("Olion (" + type + ") luonti ei onnistunut");
			e.printStackTrace();
		}
		return uusi;
	}

	@Override
	public void lue(File dir) throws StorageException {
		File file = new File(dir, tiedostonimi + Constants.FILE_EXT);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.equals("") || line.charAt(0) == Constants.COMMENT) {
					continue;
				}
				T uusi = luoUusi();
				try {
					uusi.parse(line);
				}
				catch (ParseException e) {
					throw new StorageException("Virheellinen datarivi @ " + type.toString() + Constants.NL + "\"" + e.getMessage() + "\"");
				}
				add(uusi);
			}
		}
		catch (FileNotFoundException e) {
			throw new StorageException("Tiedosto ei aukea! " + e.getMessage());
		}
		catch (IOException e) {
			throw new StorageException("Virhe! " + e.getMessage());
		}
		this.muutettu = false;
	}

	@Override
	public void save(File dir) throws StorageException {
		if (!this.muutettu) {
			return;
		}

		String fileName = tiedostonimi + Constants.FILE_EXT;
		String bkName = tiedostonimi + Constants.BAK_EXT;

		File file = new File(dir, fileName);
		File bak = new File(dir, bkName);
		Path pFile = file.toPath();
		Path pBak = bak.toPath();

		try {
			Files.deleteIfExists(pBak);
			if (file.exists()) {
				Files.move(pFile, pBak);
			}
			Files.createFile(pFile);
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new StorageException("Tallennus ei onnistunut (" + e.getMessage() + ")", e.getCause());
		}

		try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
			for (T unit : getItems()) {
				StringBuilder sb = new StringBuilder();
				String sep = "";
				String[] fields = unit.getFieldsAsStrings();
				for (String s : fields) {
					sb.append(sep);
					sep = Constants.DB_SEP;
					sb.append(s);
				}
				pw.println(sb);
			}
		}
		catch ( FileNotFoundException ex ) {
			throw new StorageException("Tiedosto " + file.getName() + " ei aukea");
		}
		catch ( IOException ex ) {
			throw new StorageException("Tiedoston " + file.getName() + " kirjoittamisessa ongelmia");
		}

		this.muutettu = false;
	}

}

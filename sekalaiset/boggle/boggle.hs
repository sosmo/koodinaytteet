import Debug.Trace
import Test.QuickCheck

import Trie (Trie)
import qualified Trie as T
import Data.Array (Array, array, (!))
import qualified Data.Array as A
import Data.Set (Set)
import qualified Data.Set as S
import System.Environment
import System.IO
import System.Directory
import System.Random


type Board = Array (Int, Int) Char

-- Luo boggle-pelilauta.
-- 1: Laudan vaaka- tai pystyrivien määrä.
-- 2: Lista merkeistä, joita sijoitetaan laudan ruutuihin. Merkit sijoitetaan riveittäin, ja listassa täytyy olla vähintään yhtä monta alkiota kuin laudalla on ruutuja.
-- 3: 2-ulotteinen Array, joka sisältää listan perusteella määritellyt merkit. Arvot on indeksoitu tupleilla muotoa '(rivi, sarake)'.
--
-- Esim:
-- 'makeBoard 2 "abcd"' luo Arrayn muotoa ["ab", "cd"]
makeBoard :: Int -> [Char] -> Array (Int, Int) Char
makeBoard size chars =
	array ((1, 1), (size, size)) (make size 1 chars)
	where
		-- kamala loopin korvike - luo "association list", jossa parametrina saadun listan arvot C-mäisesti indeksoituna rivi riviltä.
		make :: Int -> Int -> [a] -> [((Int, Int), a)]
		make size i xs
			| i > size = []
			| otherwise =
				let (h, t) = splitAt size xs
				in
				makeInner i 1 h ++ make size (i+1) t
		makeInner :: Int -> Int -> [a] -> [((Int, Int), a)]
		makeInner _ _ [] = []
		makeInner i j (x:xs) = ((i, j), x):(makeInner i (j+1) xs)

boardSize :: Array (Int, Int) Char -> Int
boardSize board =
	let ((li, _), (hi, _)) = A.bounds board
	in hi - li + 1

-- Etsi kaikki halutusta pelilaudan ruudusta alkavat sanat.
-- 1: Aloitusruudun koordinaatit.
-- 2: Käytössä olevat sanat.
-- 3: Käytettävä pelilauta.
-- 4: Set, jossa kaikki löydetyt sanat.
solveCell :: (Int, Int) -> Trie Char -> Board -> Set String
solveCell i wordsTrie board =
	let
		-- Tarkista, että aloitusruudussa on sopiva merkki ennen kuin alat tutkia ympäröiviä ruutuja.
		candChars = T.children wordsTrie
		char = board ! i
		Just subChars = T.subTrie [char] wordsTrie
	in
	if elem char candChars
		then solveHelp i "" S.empty subChars board
		else S.empty

	where
		size = boardSize board

		-- Sisällytä indeksistä löytyvään merkkiin päättyvä sana kertyneeseen tulokseen, jos sellainen löytyy, ja käy läpi ympäröivät ruudut rekursiivisesti.
		-- 1: tutkittava ruutu; 2: aiemmin kertynyt sana; 3: aiemmin käytetyt ruudut; 4: trie, jossa tästä ruudusta alkaen käytössä olevat sanat; 5: pelilauta; 6: joukko, jossa ruutuun mahdollisesti päättyvä sana ja kaikki muut ruudusta alkavat/jatkuvat sanat
		solveHelp :: (Int, Int) -> [Char] -> Set (Int, Int) -> Trie Char -> Board -> Set String
		solveHelp i@(row, col) word visited wordsTrie board =
			let
				char = T.root wordsTrie
				newWord = word ++ [char]
				-- jos kertynyt sana päättyy tähän ruutuun, lisätään tämä sana löytyneiden sanojen joukkoon
				isEndChar = T.isEndNode wordsTrie
				newVisited = S.insert i visited
				-- vaihtoehdot, joilla sanaa voi jatkaa, ja joista seuraavien tutkittavien merkkien siis pitää löytyä
				nextChars = T.children wordsTrie
				nextIndices = [(i, j) | i <- [row-1, row, row+1], j <- [col-1, col, col+1]]

				-- tarkista, voiko ruudusta etsiä sanoja, ja jos voi, kutsu solveHelpiä uudelle ruudulle päivitetyillä tiedoilla, muuten palauta tyhjä joukko
				handleChar :: (Int, Int) -> Set String
				handleChar index@(i, j) =
					let
						isValidIndex = i > 0 && i <= size && j > 0 && j <= size
						isNotVisited = S.notMember index newVisited
						char = board ! index
						isValidChar = elem char nextChars
						Just subChars = T.subTrie [char] wordsTrie
						--dbg = trace (show isValidIndex ++ show isValidChar ++ show isNotVisited ++ show index ++ show char) True
					in
					--if dbg && isValidIndex && isValidChar && isNotVisited then
					if isValidIndex && isNotVisited && isValidChar
						then solveHelp index newWord newVisited subChars board
						else S.empty

				-- kerää sanat ympäröivistä ruuduista
				nextResults = S.unions $ map handleChar nextIndices
				--dbg = traceShow nextChars True
			in
			--if dbg && isEndChar then
			if isEndChar
				then S.insert newWord nextResults
				else nextResults

-- Etsi kaikki mahdolliset sanat pelilaudalta.
-- 1: Käytössä olevat sanat.
-- 2: Käytettävä pelilauta.
-- 3: Set, jossa kaikki löydetyt sanat.
solve :: Trie Char -> Board -> Set String
solve allWords board =
	let size = boardSize board
	in
	foldr
		(\ row foundWords ->
			S.union
				(foldr
					(\ col foundWords ->
						S.union
							(solveCell (row, col) allWords board)
							foundWords)
					S.empty
					[1 .. size])
				foundWords)
		S.empty
		[1 .. size]

-- Näytä pelilauta kivassa tekstimuodossa.
showBoard :: Board -> String
showBoard board =
	let
		size = boardSize board
		rows =
			foldr
				(\ row acc ->
					let
						(_:rowFolded) =
							foldr
								(\ col acc ->
									(' ':(board ! (row, col)):[]) ++ acc)
								[]
								[1 .. size]
					in
					rowFolded:acc)
				[]
				[1 .. size]
		horizBorder = replicate (size*2 + 1) '-'
	in
	horizBorder
		++ "\n"
		++ unlines (map (\ x -> "|" ++ x ++ "|") rows)
		++ horizBorder
		++ "\n"

-- Pelaa bogglea konetta vastaan.
playGame :: (Trie Char) -> IO ()
playGame trie = do
		rnd <- getStdGen
		let
			rndChars = randomRs ('a', 'z') rnd
			board = makeBoard 6 rndChars
			--board = makeBoard 3 "auxtokxxk"
			--board = makeBoard 3 "auxtkkxxx"
		--print (solveCell (1,1) trie board)
		putStrLn "Pelilauta:"
		putStrLn (showBoard board)
		putStrLn "Syötä löytämäsi sanat pilkuilla erotettuna! Äläkä huijaa!"
		line <- getLine
		putStrLn "\nTietokoneen löytämät sanat:"
		let
			words = S.toAscList $ solve trie board
			(_, formattedWords) = foldr (\ x (first, acc) ->
						if first then (False, x ++ acc) else (False, x ++ ", " ++ acc))
					(True, [])
					words
		putStrLn formattedWords
		let playerWordCount = (+ 1) . length . filter (== ',') $ line
		putStrLn $ "\nTietokone löysi: " ++ show (length words)
				++ "\nSinä löysit: " ++ show playerWordCount

-- Lue mahdollinen tiedostonimi ekasta argumentista tai avaa "word_list.txt", ja pelaa tiedostosta löytyvän sanalistan kanssa bogglea.
main = do
		args <- getArgs
		let fPath = if length args > 0 then head args else "./word_list.txt"
		fHandle <- openFile fPath ReadMode
		fileOk <- hIsReadable fHandle
		if fileOk
			then
			do
				hSetBuffering fHandle $ BlockBuffering (Just 2048)
				contents <- hGetContents fHandle
				let trie = T.fromList (lines contents)
				--putStrLn (unlines (map (\ x -> show x ++ "\n\n") (T.subTries trie)))
				playGame trie
			else
				putStrLn "Sanalistan lukeminen ei onnistunut!"
		hClose fHandle



-- ==================================================
-- Testiosio
-- ==================================================

{-
makeBoard_sizeOk :: Int -> Bool
makeBoard_sizeOk size =
	let
		board = makeBoard size (repeat 'a')
		((li, lj), (hi, hj)) = A.bounds board
		height = hi - li + 1
		width = hj - lj + 1
	in
	height == width && height == size

main = do
		quickCheck makeBoard_sizeOk
-}

module Trie (
empty,
singleton,
insert,
subTrie,
subTries,
root,
isEndNode,
children,
fromList,
Trie)
where

import Test.QuickCheck
import qualified Data.List as L

import Data.Map.Lazy (Map, (!))
import qualified Data.Map.Lazy as M


type IsEndNode = Bool

-- Trie - 1: solmuun säilöttävä arvo; 2: päättääkö solmu jonkin validin listan; 3: alipuut
-- Leaf - 1: solmuun säilöttävä arvo; 2: päättääkö solmu jonkin validin listan
-- Root - 1: alipuut
-- Empty tarvitaan vain tyhjälle puulle
data Trie a = Node a IsEndNode (Map a (Trie a)) | Leaf a IsEndNode | Root (Map a (Trie a)) | Empty deriving (Show, Eq)


-- Lisää listan alkioista muodostuva "puun haara" halutun puun lapseksi.
-- 1: Lista lisättävistä alkioista.
-- 2: Trie, jonka johonkin lapsisolmuun arvolistasta muodostuva alipuu lisätään.
-- 3: Päivitetty trie.
insert :: (Ord a) => [a] -> Trie a -> Trie a
-- jos lisätään tyhjää listaa trien alipuuksi, niin silloin alkuperäisen listan vika alkio meni tähän puuhun minkä alle ollaan nyt lisäämässä. tätä ei siis tarvitse muuttaa paitsi merkitä endNodeksi
insert [] (Node y _ nodes) = Node y True nodes
insert [] (Leaf y _) = Leaf y True
insert [] node = node
insert xs (Leaf y isEnd) = Node y isEnd (insertIntoNodes xs M.empty)
insert xs (Node y isEnd nodes) = Node y isEnd (insertIntoNodes xs nodes)
insert xs (Root nodes) = Root (insertIntoNodes xs nodes)
insert xs Empty = Root (insertIntoNodes xs M.empty)

-- Päivitä olemassaolevat solmut lisäämällä arvolista alkamaan oikean solmun (tai tarvittaessa uuden solmun) kohdalta.
-- 1: Lista lisättävistä alkioista.
-- 2: Mappi käsiteltävistä puista/solmuista.
-- 3: Mappi päivitettynä.
insertIntoNodes :: (Ord a) => [a] -> Map a (Trie a) -> Map a (Trie a)
insertIntoNodes (val:vals) nodes =
	let
		-- luo uusi tai päivitä vanha node, ja lisää siihen loppu lista insertillä
		newNode =
			if M.member val nodes then
				insert vals (nodes ! val)
			else
				insert vals (Leaf val False)
	in
	M.insert val newNode nodes

-- Hae triestä alipuu, joka alkaa kohdasta, johon päästään seuraamalla annettua listaa trien alipuihin.
-- 1: Lista arvoista, jonka alkioiden mukaan valitaan aina mihin alipuuhun siirrytään juuresta alkaen.
-- 2: joo
-- 3: Nothing, jos jossain kohtaa listan alkiota vastaavaa alipuuta ei löydy, muuten Just alipuu.
subTrie :: (Ord a) => [a] -> Trie a -> Maybe (Trie a)
subTrie [] _ = Nothing
subTrie [x] (Node _ _ nodes) = M.lookup x nodes
subTrie [x] (Root nodes) = M.lookup x nodes
subTrie (x:xs) (Node _ _ nodes)
	| M.member x nodes = subTrie xs (nodes ! x)
	| otherwise = Nothing
subTrie (x:xs) (Root nodes)
	| M.member x nodes = subTrie xs (nodes ! x)
	| otherwise = Nothing
subTrie _ _ = Nothing

-- Puun suorat alipuut.
-- 1: puu
-- 2: Lista alipuista.
subTries :: (Ord a) => Trie a -> [Trie a]
subTries (Node _ _ nodes) = map (\(k, v) -> v) $ M.toList nodes
subTries (Root nodes) = map (\(k, v) -> v) $ M.toList nodes
subTries _ = []

-- Juuren sisältämä arvo.
root :: Trie a -> a
root (Node x _ _) = x
root (Leaf x _) = x
root _ = error "Ei voida ottaa juurta ylimmästä solmusta"

-- Onko juuri jonkin isomman arvon, sanan tms, loppualkio?
isEndNode :: Trie a -> Bool
isEndNode (Node _ isEnd _) = isEnd
isEndNode (Leaf _ isEnd) = isEnd
isEndNode _ = False

-- Puun suorat lapsiarvot.
children :: Trie a -> [a]
children trie =
	case trie of
		(Node _ _ nodes) -> childrenToList nodes
		(Root nodes) -> childrenToList nodes
		_ -> []
	where
		childrenToList =
			M.foldr
				(\ trie acc ->
					case trie of
						Leaf x _ -> x:acc
						Node x _ _ -> x:acc)
				[]

empty :: Trie a
empty = Empty

singleton :: (Ord a) => a -> Trie a
singleton x = insert [x] empty

fromList :: (Ord a) => [[a]] -> Trie a
fromList xs = listHelp xs empty
	where
		listHelp :: (Ord a) => [[a]] -> Trie a -> Trie a
		listHelp [] trie = trie
		listHelp (x:xs) trie = listHelp xs (insert x trie)


--main = do
--	let
--		alku = empty :: Trie Char
--		x = insert "aa" alku
--		x' = insert "ak" x
--		x'' = insert "kaö" x'
--		x''' = insert "a" x''
--		z = insert "auu" x'''
--		Just z' = subTrie "a" z
--		z'' = z'
--		a = z''
--	print a
--	print $ children a
--	print $ subTries a
--	print $ subTrie "uu" a



-- ==================================================
-- Testiosio
-- ==================================================

empty_singleton_subtrie_insert_ok :: [Int] -> Bool
empty_singleton_subtrie_insert_ok [] = True
empty_singleton_subtrie_insert_ok (x:xs) = (root <$> subTrie [x] (insert [x] empty)) == (root <$> subTrie [x] (singleton x))

subtries_countOk :: [[Int]] -> Bool
subtries_countOk xs =
	let
		trie = fromList xs
		noEmpty = filter (\ x -> x /= []) xs
		noSame = L.nubBy (\(x1:_) (x2:_) -> x1 == x2) noEmpty
	in
	length (subTries trie) == length noSame

children_ok :: [[Int]] -> Bool
children_ok xs =
	let
		trie = fromList xs
		noEmpty = filter (\ x -> x /= []) xs
		noSame = L.nubBy (\(x1:_) (x2:_) -> x1 == x2) noEmpty
		heads = map (\(x:_) -> x) noSame
	in
	L.sort (children trie) == L.sort heads

isEndNode_ok :: [[Int]] -> [Int] -> Bool
isEndNode_ok [] _ = True
isEndNode_ok [[]] _ = True
isEndNode_ok _ [] = True
isEndNode_ok xs val =
	let
		trie = fromList xs
		newTrie = insert val trie
	in
	(isEndNode <$> subTrie val newTrie) == Just True

subTries_subTrie_eq :: [Int] -> Bool
subTries_subTrie_eq [] = True
subTries_subTrie_eq xa@(x:xs) =
	let trie = insert xa empty
	in
	Just (head (subTries trie)) == subTrie [x] trie

main = do
		quickCheck empty_singleton_subtrie_insert_ok
		quickCheck subtries_countOk
		quickCheck children_ok
		quickCheck isEndNode_ok

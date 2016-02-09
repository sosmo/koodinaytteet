{-# LANGUAGE OverloadedStrings #-}
{-# LANGUAGE DeriveGeneric #-}

import Control.Monad
import Data.Map.Lazy (Map)
import qualified Data.Map.Lazy as M
import GHC.Generics

import Network.HTTP
import Data.Aeson (FromJSON, ToJSON, (.:))
import qualified Data.Aeson as JS

import qualified Data.ByteString.Internal as B1
import qualified Data.ByteString.Lazy as B2
import qualified Data.Char as CH

import Web.Scotty
import qualified Web.Scotty as S
import Text.Blaze.Html5 hiding (main, map, param)
import qualified Text.Blaze.Html5 as H
import Text.Blaze.Html5.Attributes
import qualified Text.Blaze.Html5.Attributes as A
import Text.Blaze.Html.Renderer.Text (renderHtml)


-- Valuuttakurssit siinä muodossa kuin ne netistä tulee
data RateDict = RateDict {
		base :: String,
		date :: String,
		rates :: Map String Double}
	deriving (Generic, Show)

instance FromJSON RateDict where
	parseJSON (JS.Object v) =
		RateDict
			<$> v .: "base"
			<*> v .: "date"
			<*> v .: "rates"
	parseJSON _ = mzero

instance ToJSON RateDict


str2ByteStr :: String -> B2.ByteString
str2ByteStr = B2.pack . map B1.c2w

toUpper :: String -> String
toUpper [] = []
toUpper (x:xs) = (CH.toUpper x):(toUpper xs)


-- IO-monadi, joka hakee netistä valuuttakurssit ja muuntaa saadun JSON:in RateDictiksi
getRates :: IO RateDict
getRates = do
		result <- simpleHTTP (getRequest "http://api.fixer.io/latest")
		json <- str2ByteStr <$> getResponseBody result
		--return $ (JS.decode json :: Maybe RateDict)
		let (Just rates) = JS.decode json :: Maybe RateDict
		return rates

-- Muunna haluttu määrä rahaa valuutasta toiseen RateDictin kurssien perusteella.
convert :: RateDict -> String -> String -> Double -> Maybe Double
convert rateDict from to amount =
	let
		(RateDict base _ rates) = rateDict
		from' = toUpper from
		to' = toUpper to
		rateFrom =
			if from' == base then
				Just 1.0
			else
				M.lookup from' rates
		rateTo =
			if to' == base then
				Just 1.0
			else
				M.lookup to' rates
	in (*) <$> ((/) <$> pure amount <*> rateFrom) <*> rateTo

-- Luo Scottyn ymmärtämä nettisivu, jossa on parametreina saatujen arvojen ja RateDictin perusteella laskettu muunnettu rahasumma tai virheviesti.
handleConvert :: RateDict -> ActionM ()
handleConvert rateDict = do
		amount <- param "amount"
		from <- param "from"
		to <- param "to"
		S.html . renderHtml $ do
				docTypeHtml $ do
					H.head $ do
						H.title "Valuuttamuunnin"
					H.body $ do
						case convert rateDict from to amount of
							Just result ->
								H.div $ toHtml . show $ result
							_ ->
								H.div "Virheellisiä valuuttanimiä joukossa!"

-- Valuuttamuunnin, portti 3000.
-- Käyttö: /1?from=eur&to=usd, missä 1 vastaa muunnettavaa summaa, eur vastaa lähtövaluuttaa, usd kohdevaluuttaa
main = do
		rates <- getRates
		scotty 3000 $ do
				get "/:amount" $ handleConvert rates
				get "/convert/:amount" $ handleConvert rates

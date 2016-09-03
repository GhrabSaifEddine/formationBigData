package com.ov.exercice.sncf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
 * Consigne:
 * Enregistrez vous sur https://data.sncf.com/api (http://www.navitia.io/register/) pour récupérer une clé d'API.
 * Ce code va servir à récupérer les horaires de trains au départ de montparnasse.
 * Puis ensuite les afficher sous forme Heure : destination
 * Vous utiliserez votre clé dans l'url d'appel de l'API.
 * Il ne respecte pas les standards et doit être nettoyé puis refactoré pour
 * être réutilisable et compréhensible.
 */

public class gettrajets {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		try {
			// Etablissement de connexion avec les données
			HttpURLConnection sConnection = (HttpURLConnection) (new URL(
					"https://api.sncf.com/v1/coverage/sncf/stop_areas/stop_area:OCE:SA:87391003/departures?datetime=20160729T150423"))
							.openConnection();
			sConnection.setRequestProperty("Authorization", "Basic "
					+ (Base64.getUrlEncoder().encodeToString("48dea024-a987-46ae-b678-8bb8d79a801d:".getBytes())));
			sConnection.setRequestMethod("GET");
			sConnection.setRequestProperty("Content-length", "0");
			sConnection.setUseCaches(false);
			sConnection.setAllowUserInteraction(false);
			sConnection.connect();
			int iResponse = sConnection.getResponseCode();
			String lFichierJson = "";
			switch (iResponse) {
			case 200:
			case 201:
				BufferedReader lReader = new BufferedReader(new InputStreamReader(sConnection.getInputStream()));
				StringBuilder lBuilder = new StringBuilder();
				String lLine;
				while ((lLine = lReader.readLine()) != null) {
					lBuilder.append(lLine + "\n");
				}
				lReader.close();
				// Mise en place des données dans un fichier JSON
				lFichierJson = lBuilder.toString();
				JSONArray lJsonArray = (JSONArray) (((JSONObject) (new JSONParser()).parse(lFichierJson))
						.get("departures"));
				System.out.println("Prochains départs de Montparnasse :");
				for (int i = 0; i < lJsonArray.size() - 1; i++) {
					// Récupération des horaires de départ
					JSONObject lHoraireDepart = (JSONObject) ((JSONObject) lJsonArray.get(i)).get("stop_date_time");
					String oHoraireDepartLabel = lHoraireDepart.get("departure_date_time").toString();
					// Récupération des chemins
					JSONObject lRoute = (JSONObject) ((JSONObject) ((JSONObject) lJsonArray.get(i)).get("route"))
							.get("line");
					String oRoute = lRoute.get("name").toString();
					// Récupération de la liste finale des trajets
					System.out.println(oHoraireDepartLabel + " : " + oRoute);
				}
			}
		} catch (MalformedURLException iException) {
			// TODO Auto-generated catch block
			System.err.println(iException);
		} catch (IOException iException) {
			// TODO Auto-generated catch block
			System.err.println(iException);
		} catch (ParseException iException) {
			// TODO Auto-generated catch block
			System.err.println(iException);
		}
	}
}

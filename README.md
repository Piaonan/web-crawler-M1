## Pour exécuter notre projet, il y a 2 façons.



# Avec le fichier jar : 
	- un double-clic sur le fichier « webCrawler.jar » 
	- ou bien en ligne de commande sur le terminal :
		java -jar webCrawler.jar
	Si vous choisissez de l'exécuter en ligne de commande, il existe quelques options que vous pouvez paramétrer. Le port du serveur « -port valeur », le nombre d'explorateurs créés « -nbExp valeur » et la limite « -limit valeur » qui est la limite d'URLs à partir de laquelle le serveur arrête de parcourir des URLs non-explorées.
	Voici un exemple avec les options : java -jar webCrawler.jar -port 7500 -nbExp 5 -limit 10
	Par défaut, si vous choisissez de ne pas paramétrer cela, sachez que le port sera 8000, le nombre d'explorateur sera 10 et la limite sera 150.


# Sinon sur Eclipse :
	Il faut mettre le dossier « src » dans votre projet, puis importer la librairie externe Json que j'ai utilisé pour le parsage.
	Pour importer la librairie, il suffit de faire un clic droit sur votre projet sur Eclipse, sélectionnez « Build Path », puis sélectionnez « Add External Archives... » et sélectionnez le fichier « minimal-json-0.9.5.jar » qui se trouve dans le dossier « librairie externe json »


Une fois sur l'application, vous pouvez entrer un lien d'une page web dans la case dédiée (exemple de lien : http://solistrad.fr/). Cliquez sur le bouton « Explorer » pour démarrer l'exploration. Après la fin de l'exploration de votre URL, vous pouvez visualiser toutes les URLs et mots trouvés avec le menu déroulant à droite ou rechercher toutes les URLs qui contiennent le mot que vous mettez sur la case dédiée.
Il y a une sauvegarde des données avant la fermeture de l'application et un chargement de donnnées avant l'ouverture de l'application.


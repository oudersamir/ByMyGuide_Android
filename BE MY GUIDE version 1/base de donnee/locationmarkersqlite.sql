-- phpMyAdmin SQL Dump
-- version 4.6.5.2
-- https://www.phpmyadmin.net/
--
-- Client :  127.0.0.1
-- Généré le :  Mer 29 Janvier 2020 à 20:40
-- Version du serveur :  10.1.21-MariaDB
-- Version de PHP :  5.6.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données :  `locationmarkersqlite`
--

-- --------------------------------------------------------

--
-- Structure de la table `locations`
--

CREATE TABLE `locations` (
  `_id` int(11) NOT NULL,
  `lat` text NOT NULL,
  `lng` text NOT NULL,
  `zoom` text NOT NULL,
  `place` text NOT NULL,
  `notes` decimal(10,0) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Contenu de la table `locations`
--

INSERT INTO `locations` (`_id`, `lat`, `lng`, `zoom`, `place`, `notes`) VALUES
(1148, '34.02375967603165', '-6.851561442017555', '15', '95 Avenue Mokhtar Gazoulit, Rabat, Maroc', '10'),
(1149, '34.02177031884771', '-6.852876394987106', '15', '272 Avenue Abdelkrim Al Khattabi, Rabat 10040, Maroc', '2'),
(1150, '34.0246266532587', '-6.848362907767296', '15', '16 Rue Accra, Rabat, Maroc', '10'),
(1156, '34.014299323037946', '-6.843833997845651', '15', '12 Avenue Abdelouahed Al Marakchi, Rabat, Maroc', '10'),
(1157, '34.02375967603165', '-6.851561442017555', '15', '95 Avenue Mokhtar Gazoulit, Rabat, Maroc', '10'),
(1158, '34.02177031884771', '-6.852876394987106', '15', '272 Avenue Abdelkrim Al Khattabi, Rabat 10040, Maroc', '8'),
(1159, '34.0246266532587', '-6.848362907767296', '15', '16 Rue Accra, Rabat, Maroc', '5'),
(1160, '34.0246266532587', '-6.848362907767296', '15', '16 Rue Accra, Rabat, Maroc', '6'),
(1161, '34.0076598', '-6.8382597', '15', 'Avenue Ibn Batouta, Rabat, Maroc', '5'),
(1162, '34.0076598', '-6.8382597', '15', 'Avenue Ibn Batouta, Rabat, Maroc', '10'),
(1163, '34.008237062175084', '-6.83818057179451', '15', 'Avenue Ibn Batouta, Rabat, Maroc', '10');

--
-- Index pour les tables exportées
--

--
-- Index pour la table `locations`
--
ALTER TABLE `locations`
  ADD PRIMARY KEY (`_id`);

--
-- AUTO_INCREMENT pour les tables exportées
--

--
-- AUTO_INCREMENT pour la table `locations`
--
ALTER TABLE `locations`
  MODIFY `_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1164;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

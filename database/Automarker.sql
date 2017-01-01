-- phpMyAdmin SQL Dump
-- version 4.6.5.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Jan 01, 2017 at 07:41 PM
-- Server version: 5.7.16-0ubuntu0.16.04.1
-- PHP Version: 7.0.8-0ubuntu0.16.04.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `Automarker`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `username` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `configuration`
--

CREATE TABLE `configuration` (
  `key` varchar(100) NOT NULL,
  `value` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `exercise`
--

CREATE TABLE `exercise` (
  `exname` varchar(100) NOT NULL,
  `extype` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `ExerciseTypes`
--

CREATE TABLE `ExerciseTypes` (
  `type` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `exercise_big_table`
--

CREATE TABLE `exercise_big_table` (
  `exname` varchar(200) NOT NULL,
  `uname` varchar(100) NOT NULL,
  `userindex` text NOT NULL,
  `hash` varchar(200) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `HaskellCategories`
--

CREATE TABLE `HaskellCategories` (
  `name` varchar(100) NOT NULL,
  `type` text NOT NULL,
  `annotation` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `HaskellTraining`
--

CREATE TABLE `HaskellTraining` (
  `name` varchar(100) NOT NULL,
  `text` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `MarkMarkerTest`
--

CREATE TABLE `MarkMarkerTest` (
  `exercise` varchar(50) NOT NULL,
  `training_data` mediumtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `MarkMarkerTraining`
--

CREATE TABLE `MarkMarkerTraining` (
  `exercise_name` varchar(50) NOT NULL,
  `training_set` longtext NOT NULL,
  `serialized` longblob NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `PdfPaths`
--

CREATE TABLE `PdfPaths` (
  `token` varchar(100) NOT NULL,
  `path` text NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `professor`
--

CREATE TABLE `professor` (
  `username` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
  `username` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `UploadPaths`
--

CREATE TABLE `UploadPaths` (
  `token` varchar(100) NOT NULL,
  `path` text NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `username` varchar(50) NOT NULL,
  `type` varchar(10) NOT NULL,
  `fullname` text NOT NULL,
  `email` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`username`);

--
-- Indexes for table `configuration`
--
ALTER TABLE `configuration`
  ADD PRIMARY KEY (`key`);

--
-- Indexes for table `exercise`
--
ALTER TABLE `exercise`
  ADD PRIMARY KEY (`exname`);

--
-- Indexes for table `ExerciseTypes`
--
ALTER TABLE `ExerciseTypes`
  ADD PRIMARY KEY (`type`);

--
-- Indexes for table `exercise_big_table`
--
ALTER TABLE `exercise_big_table`
  ADD PRIMARY KEY (`exname`,`uname`,`hash`);

--
-- Indexes for table `HaskellCategories`
--
ALTER TABLE `HaskellCategories`
  ADD PRIMARY KEY (`name`);

--
-- Indexes for table `HaskellTraining`
--
ALTER TABLE `HaskellTraining`
  ADD KEY `name` (`name`);

--
-- Indexes for table `MarkMarkerTraining`
--
ALTER TABLE `MarkMarkerTraining`
  ADD PRIMARY KEY (`exercise_name`);

--
-- Indexes for table `PdfPaths`
--
ALTER TABLE `PdfPaths`
  ADD PRIMARY KEY (`token`);

--
-- Indexes for table `professor`
--
ALTER TABLE `professor`
  ADD PRIMARY KEY (`username`);

--
-- Indexes for table `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`username`);

--
-- Indexes for table `UploadPaths`
--
ALTER TABLE `UploadPaths`
  ADD PRIMARY KEY (`token`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`username`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `HaskellTraining`
--
ALTER TABLE `HaskellTraining`
  ADD CONSTRAINT `name_fk` FOREIGN KEY (`name`) REFERENCES `HaskellCategories` (`name`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `MarkMarkerTraining`
--
ALTER TABLE `MarkMarkerTraining`
  ADD CONSTRAINT `type_fk` FOREIGN KEY (`exercise_name`) REFERENCES `ExerciseTypes` (`type`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

show databases;

use HospitalManagementSystem;

show tables;

-- Feedback
drop table if exists feedback;

create table feedback(
	feedbackNo int auto_increment,
	patientID varchar(255),
    employeeID varchar(255),
    review varchar(255),
    primary key (feedbackNo)
);

describe feedback;

select * from feedback;

-- Emloyee
drop table if exists employee;

create table employee(
		name varchar(255),
        age int,
        employeeID int,
        salary float,
        designation varchar (255),
        email varchar (255),
        primary key (employeeID)
);

describe employee;
-- Login
drop table if exists loginCredentials;

create table loginCredentials(
		userID varchar(255),
        password varchar(255),
        primary key (userID)
);

describe loginCredentials;

select * from loginCredentials;

-- Appointment
drop table if exists Appointment;

create table Appointment(
		appID int auto_increment,
        doctorID varchar(255),
        patientID varchar(255),
        appDate timestamp,
        wardID int,
        slot int,
        primary key (appID)
);

describe Appointment;

insert into Appointment(doctorID, slot) values (?, ?);

-- Pharmacy 
drop table if exists Pharmacy ;

create table Pharmacy (
		medID int auto_increment,
        medName varchar (255),
        price float,
        maxNo int,
        primary key (medID)
);

describe Pharmacy ;

select * from Pharmacy;

-- Medicine
drop table if exists Medicine ;

create table Medicine (
		medID int auto_increment,
        medName varchar(255),
        category varchar(255),
        dosage int,
        primary key (medID)
);

describe Medicine ;

-- PaymentRecord
drop table if exists PaymentRecord ;

create table PaymentRecord (
		paymentID int,
        amount float,
        primary key (paymentID)
);

describe PaymentRecord ;

-- PatientRecord
drop table if exists PatientRecord ;

create table PatientRecord (
		recordID int,
        patientID int,
        employeeID int,
        appDate timestamp,
        medID int,
        wardID int,
        paymentID int,
        primary key (recordID)
);

describe PatientRecord ;

-- Ward
drop table if exists Ward ;

create table Ward (
		wardName varchar(255),
        wardID int,
        occupied bool,
        -- patientID int,
        primary key (wardID)
);

describe Ward ;

-- Patient

drop table if exists Patient ;

create table Patient (
		patientName varchar(255),
        age int,
        patientID int,
        address varchar(255),
        visitCount int,
        gender varchar(255),
        primary key (patientID)
);

describe Patient ;

-- Doctor 

drop table if exists Doctor ;

create table Doctor (
		specialisation varchar(255),
        employeeID varchar(255),
        docName varchar(255),
        salary float,
        primary key (employeeID)
);

insert into Doctor values('Cardiologist', 'd001', 'Chris Pine', 30000);
insert into Doctor values('Dermatologist', 'd002', 'Charley Davis', 40000);
insert into Doctor values('Psychiatrist', 'd003', 'Albert P', 50000);
insert into Doctor values('Oncologists', 'd004', 'Timothy Grayscale', 300000);
insert into Doctor values('Pediatricians', 'd005', 'John Snow', 3000000);

describe Doctor ;

-- Resident

drop table if exists Resident ;

create table Resident (
		name varchar(255),
        residentID int,
        doctorID int,
        salary float,
        primary key (residentID)
);

describe Resident ;

show tables;


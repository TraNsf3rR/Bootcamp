# Shelter Starter

## Purpose

This project is a simple sample application for learning basic UI development with Java and Spring Boot. It is intended for coursework, experimentation, and local practice.

## Disclaimer

This repository contains a sample homework project created for an educational course on basic UI development. It is shared for learning purposes, does not represent an official employer product or service, and does not contain proprietary or confidential materials.

## What is already provided

- sample animal data in memory
- service/repository logic for loading and saving animals
- image fallback handling by animal type
- static HTML drafts for the home page, list page, and add-animal page
- an API controller, DTOs and adoption-related support logic

## What you must build

- move the static pages into Thymeleaf templates
- create a page controller for '/', '/animals', and 'animals/new'
- connect the list page to live data with 'th:each'
- bind the form to 'AnimalForm' with 'th:object' and 'th:field'
- submit the form and redirect back to the list

## What to notice

- the repo is starter code, not finished solution
- the in-memory repository resets when the app restarts
- image values can be either a filename or a full URL
- if no image is provided, a fallback image is chosen by animal type

## Optional bonus

Add inline validation messages below the form inputs

## Animal Images

The project supports local image files from static resources.

- Add custom animal photos to `src/main/resources/static/images/animals/`
- Store only the filename in the animal field, for example `Luna.jpeg`
- The app resolves that value to `/images/animals/Luna.jpeg`
- A full `http://` or `https://` image URL is also accepted and used as-is (see the seeded Rabbit/Pepper entry for a working example).

If image filename/url is missing, a fallback is selected by animal type:

- CAT -> `/images/fallback/fallback-cat.jpg`
- DOG -> `/images/fallback/fallback-dog.jpg`
- OTHER -> `/images/fallback/fallback-other.jpg`
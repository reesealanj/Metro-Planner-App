# Metro-Planner-App
A mobile application project for my CS4237 (Software Design for Handheld Devices) Course written in Kotlin in the Spring 2020 Semester.

# Libraries/APIs Used
- Google Plays Services 
    - Google Maps/Geocoding
- Square's OkHttp Library 
- Jetbrains' Anko Networking Library
- WMATA Station API
- Yelp Businesses API

# Project Description
Given a user's location and certain preferences, the Application utilizes the WMATA Metro API as well as the Yelp Businesses API in order to display a map with a number of related attractions/restaurants centered around a location and provides the nearest Metro stop (if the target location is within the WMATA service area).

# Example Usage Screenshots
On the Home Screen, the user inputs their address to be geocoded for the map screen as well as some basic preferences as to what they want to do.
![Home Screen Image](https://github.com/reesealanj/Metro-Planner-App/blob/master/img/DetailsScreen.png)

On the Maps screen, the points gathered from the APIs are color coded and plotted on a map, with the option to retun to the preferences screen or proceeed to a cardview screen with the results.
![Maps Screen Image](https://github.com/reesealanj/Metro-Planner-App/blob/master/img/MapScreen.png)

On the Details screen, a RecyclerView shows cards with information about each attraction listed as well as its yelp rating, "$$" tag from yelp, and information to contact the business, the address, and a link to the website.
![Details Screen Image](https://github.com/reesealanj/Metro-Planner-App/blob/master/img/DetailsScreen.png)
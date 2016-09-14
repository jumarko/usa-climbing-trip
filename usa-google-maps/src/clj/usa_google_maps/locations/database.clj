(ns usa-google-maps.locations.database)

(defrecord Location [name 
                     lat ;; latitude
                     lng ;; longitude
                     uri ;; Main web page
                     tags ;; set of tags in form of keywords that denotes type of location, e.g. :climbing, :city, :national-park
                     details ;; map with optional detail - any custom data can be provided
                     ])

(defn make-location [name lat lng & [uri tags details]]
  (map->Location {:name name
                  :lat lat
                  :lng lng
                  :uri uri
                  :tags (set  tags)
                  :details details}))



;;; cities
;;; TODO: add web pages for cities?
(def los-angeles-airport (make-location "Los Angeles Airport" 33.942791 -118.410042 ""
                           #{:city}))
(def san-francisco (make-location "San Francisco" 37.773972 -122.431297 ""
                     #{:city}))
(def salt-lake-city (make-location "Salt Lake City" 40.758701 -111.876183 ""
                      #{:city}))


;;; rock climbing areas
(def oregon-smith-rock (make-location "Oregon - Smith Rock" 44.3657, -121.143
                         "https://www.mountainproject.com/v/smith-rock/105788989"
                         #{:climbing}))

(def oregon-trout-creek (make-location "Oregon - Trout Creek" 44.802 -121.1086
                          "https://www.mountainproject.com/v/trout-creek/106505473"
                          #{:climbing}))

(def idaho-city-of-rocks (make-location "Idaho - City of Rocks" 42.0778 -113.724
                           "https://www.mountainproject.com/v/city-of-rocks/105739322"
                           #{:climbing}))

(def utah-american-fork (make-location "Utah - American Fork Canyon" 40.4323 -111.751
                          "https://www.mountainproject.com/v/american-fork-canyon/105739274"
                          #{:climbing}))

(def utah-moab-wall-street (make-location "Utah - Moab - Wall Street" 38.54669 -109.59961
                             "https://www.mountainproject.com/v/wall-street/105716961"
                             #{:climbing}))

(def utah-moab-indian-creek (make-location "Utah - Moab - Indian Creek" 38.0258 -109.54
                              "https://www.mountainproject.com/v/indian-creek/105716763"
                              #{:climbing}))

(def utah-logan (make-location "Utah - Logan" 41.7435 -111.795
                  "https://www.mountainproject.com/v/logan/105739310"
                  #{:climbing}))

(def utah-maple-canyon (make-location "Maple Canyon" 39.5552 -111.6832
                         "https://www.mountainproject.com/v/maple-canyon-road/106479926"
                         #{:climbing}))

(def nevada-red-rocks (make-location "Nevada - Red Rocks" 36.13129 -115.42453
                        "https://www.mountainproject.com/v/red-rock/105731932"
                        #{:climbing}))

(def colorado-rifle (make-location "Colorado - Rifle" 39.7159 -107.6912
                      "https://www.mountainproject.com/v/rifle-mountain-park/105744310"
                      #{:climbing}))

(def colorado-shelf-road (make-location "Colorado - Shelf Road" 38.6296 -105.223
                           "https://www.mountainproject.com/v/shelf-road/105744267"
                           #{:climbing}))

(def colorado-boulder-canyon (make-location "Colorado - Boulder Canyon" 40.0024 -105.4102
                               "https://www.mountainproject.com/v/boulder-canyon/105744222"
                               #{:climbing}))

(def california-owens-river-gorge (make-location "California - Owens River Gorge" 37.44577 -118.57168
                                    "https://www.mountainproject.com/v/owens-river-gorge/105843226"
                                    #{:climbing}))

(def california-needles (make-location "California - Needles" 36.1214 -118.5044
                          "https://www.mountainproject.com/v/the-needles--kern-river/105834180"
                          #{:climbing}))

(def california-alabama-hills (make-location "California - Alabama Hills" 36.59552 -118.1086
                                "https://www.mountainproject.com/v/alabama-hills/105876822"
                                #{:climbing}))

(def california-buttermilk-bouldering (make-location "California - Buttermilk Country - bouldering" 37.3291 -118.5771
                                        "https://www.mountainproject.com/v/buttermilk-country/105876411"
                                        #{:climbing}))

(def arizona-cochise-stronghold (make-location "Arizona - Cochise Stronghold" 31.9212 -109.98703
                                  "http://www.mountainproject.com/v/cochise-stronghold/105738034"
                                  #{:climbing}))

(def oklahoma-wichita (make-location "Oklahoma - Wichita Wildlife Refuge" 34.7108 -98.6233
                        "https://www.mountainproject.com/v/wichita-wildlife-refuge/105858670"
                        #{:climbing}))


;;; National parks
(def yellowstone (make-location "Yelowstone national park" 44.4279675 -110.5972089
                   "https://www.mountainproject.com/v/yellowstone-national-park/105964751"
                   #{:national-park}))
(def grand-teton (make-location "Grand Teton national park" 43.8075909 -110.8285948
                   "https://www.mountainproject.com/v/grand-teton-national-park/105802912"
                   #{:national-park}))
(def rocky-mountain (make-location "Rocky Mountains national park" 40.3772059 -105.5216651
                      "https://www.mountainproject.com/v/rmnp---rock/105744460"
                      #{:national-park}))
(def yosemite (make-location "Yosemite national park - Half Dome" 37.7775114 -119.6157417
                "https://www.mountainproject.com/v/yosemite-national-park/105833381"
                #{:national-park :climbing}))
(def zion (make-location "Zion national park" 37.2982022 -113.0263005
            "https://www.mountainproject.com/v/zion-national-park/105716799"
            #{:national-park :climbing}))
(def joshua-tree (make-location "Joshua Tree national park" 33.873415 -115.9009923
                   "https://www.mountainproject.com/v/joshua-tree-national-park/105720495"
                   #{:national-park :climbing}))
(def death-valley (make-location "Death Valley national park" 36.5053891 -117.0794078
                    "https://www.nps.gov/deva/learn/nature/weather-and-climate.htm"
                    #{:national-park}))
(def grand-canyon (make-location "Grand Canyon national park" 36.0600031 -112.1243856
                    "https://www.mountainproject.com/v/grand-canyon-national-park/105787841"
                    #{:national-park}
                    {:uri2 "https://www.nps.gov/deva/index.htm"}))


(def locations [los-angeles-airport
                san-francisco
                salt-lake-city

                oregon-smith-rock
                oregon-trout-creek
                idaho-city-of-rocks
                utah-american-fork
                utah-moab-wall-street
                utah-moab-indian-creek
                utah-logan
                utah-maple-canyon
                nevada-red-rocks
                colorado-rifle
                colorado-shelf-road
                colorado-boulder-canyon
                california-owens-river-gorge
                california-needles
                california-alabama-hills
                california-buttermilk-bouldering
                arizona-cochise-stronghold
                oklahoma-wichita

                grand-canyon
                yellowstone
                grand-teton
                rocky-mountain
                yosemite
                zion
                joshua-tree
                death-valley
                grand-canyon
                ])



(def road-trip [san-francisco
                yosemite
                oregon-smith-rock
                idaho-city-of-rocks
                utah-logan
                grand-teton
                rocky-mountain
                colorado-boulder-canyon
                colorado-shelf-road
                colorado-rifle
                utah-moab-wall-street
                utah-moab-indian-creek
                zion
                grand-canyon
                nevada-red-rocks
                death-valley
                california-owens-river-gorge
                joshua-tree
                los-angeles-airport])

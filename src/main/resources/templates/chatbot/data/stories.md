## story_001
* greeting
  - utter_greet
* get_snack
  - utter_ask_snack_quantity
* get_snack{"cinema_sign": "Snack"}
  - slot{"cinema_sign": "Snack"}
  - get_response
  - utter_subscribe


## story_002
* greeting
  - utter_greet
* get_snack{"cinema_sign": "Snack"}
  - slot{"cinema_sign": "Snack"}
  - get_response
  - utter_subscribe
* subscription
  - slot{"subscribe": "True"}
  - subscribe_user
* goodbye
  - utter_goodbye

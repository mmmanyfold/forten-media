(ns forten-media.views.our-difference
  (:require [re-frame.core :as re-frame]
            [forten-media.components.footer :refer [footer-component]]
            [re-com.core :as re-com]
            [forten-media.subs :as subs]))


(defn our-difference-view []
  [:div.wrapper
   [:div.left
    [:div.display.w-70.w-100-small
     [:h3 "photo"]
     [:h3 "forten media"]
     [:h2 "happy people, " [:span "2017"]]
     [:img {:src "../img/happy-people.png"}]
     [:h1 "OUR " [:span "DIFFERENCE"]]
     [footer-component "10"]]]
   [:div.right
    [:div.w-70.text.w-100-small
     [:p [:span.quote
          "“Each of you should use whatever gift you have received to serve others, as faithful stewards of God’s grace in its various forms.”"]
         [:br]
         "1 Peter 4:10"]]]])

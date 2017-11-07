(ns forten-media.views.about-us
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [forten-media.subs :as subs]))


(defn about-us-view []
  [:div.wrapper
   [:div.left "this is the about us view"]
   [:div.right
    [:div.display
     [:img {:src "../img/recipe.png"}]
     [:h2 "burger bowl recipe, " [:span "2017"]]
     [:h3 "form star living"]
     [:h3 "video"]
     [:h3 "1080x1080"]]]])

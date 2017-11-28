(ns forten-media.views.home
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [forten-media.subs :as subs]))


(defn home-view []
  [:div.wrapper
    [:div.left
     [:div.display.w-70.hide-small
      [:img {:src "../img/demo-reel.png"}]
      [:h2 "demo reel, " [:span "2017"]]
      [:h3 "forten media, llc"]
      [:h3 "video"]
      [:h3 "1920x804"]]]
    [:div.right
     [:div.toc
      [:img {:src "../img/logo.png"}]
      [:div.chapter
       [:h1.number "4"]
       [:h1 "intro" [:span "duction"]]]
      [:div.chapter
       [:h1.number "10"]
       [:h1 "about " [:span "us"]]
       [:p [:span "our "] "people"]
       [:p [:span "our "] "story"]
       [:p [:span "our "] "difference"]]
      [:div.chapter
       [:h1.number "14"]
       [:h1 [:span "our " ]"work"]
       [:p "projects"]
       [:p [:span "how it "] "works"]]
      [:div.chapter
       [:h1.number "410"]
       [:h1 "connect"]
       [:p "contact"]
       [:p "careers"]
       [:p "faqs"]]]]])

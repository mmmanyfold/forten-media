(ns forten-media.views.connect
  (:require [forten-media.components.footer :refer [footer-large footer-small]]))


(defn connect-view []
  [:div.wrapper
   [:div.left
    [:div.w-80.text.w-100-small
     [:h1 [:span "CONNECT"]]
     [:p "Hello, I'm "
      "your name"
      " and I would like to be contacted at "
      "your e-mail/phone number"
      " to collaborate on a project called "
      "project name"
      ". Here are the details: "
      "______________"]
     [footer-large "410"]]]
   [:div.right
    [:div.display.w-70.w-100-small
     [:h3 "photo"]
     [:h3 "footnote project"]
     [:h2 "special kneads and treats, " [:span "2018"]]
     [:img {:src "../img/connect.png"}]
     [footer-small "410"]]]])

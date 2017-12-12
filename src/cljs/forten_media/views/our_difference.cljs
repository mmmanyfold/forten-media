(ns forten-media.views.our-difference
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [forten-media.subs :as subs]))


(defn our-difference-view []
  [:div.wrapper
   [:div.left
    [:div.display.w-70
     [:h3 "photo"]
     [:h3 "forten media"]
     [:h2 "happy people, " [:span "2017"]]
     [:img {:src "../img/happy-people.png"}]
     [:h1 "OUR " [:span "DIFFERENCE"]]
     [:div.footer-wrap.w-80
      [:div.footer
       [:div [:a {:href "/"}
              "< table of contents"]]
       [:div {:style {:text-align "center"}} "14"]
       [:div ""]]]]]
   [:div.right
    [:div.w-70.text
     [:p [:span.quote
          "“Each of you should use whatever gift you have received to serve others, as faithful stewards of God’s grace in its various forms.”"]
         [:br]
         "1 Peter 4:10"]]]])

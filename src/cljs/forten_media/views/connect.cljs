(ns forten-media.views.connect
  (:require [reagent.core :as r]
            [forten-media.components.footer :refer [footer-large footer-small]]))

(def forms (r/atom {:client {:show true
                             :fields {:name nil
                                      :contact nil
                                      :project nil
                                      :details nil}}
                    :job {:show true
                          :fields {:name nil
                                   :contact nil
                                   :resume-file nil
                                   :demo-link nil
                                   :cover-letter nil}}}))

(defn handle-text-input [e form field]
 (let [val (-> e .-target .-value)]
   (swap! forms assoc-in [form :fields field] val)
   (if (= form :client)
     (swap! forms assoc-in [:job :show]
            (every? empty? (-> @forms :client :fields vals)))
     (swap! forms assoc-in [:client :show]
            (every? empty? (-> @forms :job :fields vals))))))


(defn connect-view []
  [:div.wrapper
   [:div.left
    [:div.w-80.text.w-100-small.connect
     [:h1 [:span "CONNECT"]]
     [:div {:class "animated fadeIn"
            :style {:display (if (-> @forms :client :show)
                               "block" "none")}}
      [:p "Hello, I'm "
       [:input {:type "text"
                :placeholder "your name"
                :style {:width "65px"}
                :on-change (fn [e] (handle-text-input e :client :name))}]

       " and I would like to be contacted at "
       [:input {:type "text"
                :placeholder "your e-mail/phone number"
                :style {:width "159px"}
                :on-change (fn [e] (handle-text-input e :client :contact))}]

       " to collaborate on a project called "
       [:input {:type "text"
                :placeholder "project name"
                :style {:width "80px"}
                :on-change (fn [e] (handle-text-input e :client :project))}]
       ". Here are the details: "

       [:input {:type "text"
                :placeholder "__________________"
                :style {:text-decoration "underline"}
                :on-change (fn [e] (handle-text-input e :client :details))}]]]

     [:div {:class "animated fadeIn"
            :style {:display (if (-> @forms :job :show)
                               "block" "none")}}
      [:p "Hello, I'm "
       [:input {:type "text"
                :placeholder "your name"
                :style {:width "65px"}
                :on-change (fn [e] (handle-text-input e :job :name))}]

       " and I would like to learn about employment opportunities at Forten Media. I can be contacted at "
       [:input {:type "text"
                :placeholder "your e-mail/phone number"
                :style {:width "159px"}
                :on-change (fn [e] (handle-text-input e :job :contact))}]

       ". My resume is "
       [:span.file-input-outer-wrap
        [:span.file-input-inner-wrap
         [:input {:type "file"}]]
        "attached here"]

       ". My demo reel can be accessed at "
       [:input {:type "url"
                :placeholder "this link"
                :style {:width "47px"}
                :on-change (fn [e] (handle-text-input e :job :demo-link))}]
       "."]

      [:div
       {:class "textarea animated fadeIn"
        :style {:display (if (every? empty? (-> @forms :job :fields vals))
                           "none" "block")}}
       "A brief cover letter is below:" [:br]
       [:textarea {:rows "3"
                   :on-change (fn [e] (handle-text-input e :job :cover-letter))}]]]

     [footer-large "410"]]]

   [:div.right
    [:div.display.w-70.w-100-small
     [:h3 "photo"]
     [:h3 "footnote project"]
     [:h2 "special kneads and treats, " [:span "2018"]]
     [:img {:src "../img/connect.png"}]

     [footer-small "410"]]]])
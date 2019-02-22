(ns forten-media.views.connect
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as r]
            [cljs.core.async :refer [<! timeout chan >! go-loop]]
            [cljs-http.client :as http]
            [forten-media.components.footer :refer [footer-large footer-small]]))

(def upload-url-endpoint "http://localhost:4000/upload-url")

(def upload-progress
  (r/atom {:percentage 0
           :complete   false}))

(def forms
  (r/atom
    {:client
     {:show   true
      :fields {:name    nil
               :contact nil
               :project nil
               :details nil}}
     :job
     {:show   true
      :fields {:name         nil
               :contact      nil
               :resume-file  nil
               :demo-link    nil
               :cover-letter nil}}}))

(defn calc-percent [upload]
  (let [{loaded :loaded total :total} upload]
    (when (every? #(> % 0) [total loaded])
      (js/Math.round (* (/ loaded total) 100)))))

(defn handle-text-input [e form field]
  (let [val (-> e .-target .-value)]
    (swap! forms assoc-in [form :fields field] val)
    (if (= form :client)
      (swap! forms assoc-in [:job :show]
             (every? empty? (-> @forms :client :fields vals)))
      (swap! forms assoc-in [:client :show]
             (every? empty? (-> @forms :job :fields vals))))))

(defn handle-upload [e]
  (when-let [file (aget (-> e .-target .-files) 0)]
    (.preventDefault e)
    (let [type (.-type file)
          size (.-size file)
          name (.-name file)]
      (go
        (let [request (http/post
                        upload-url-endpoint
                        {:json-params {:type type
                                       :size size
                                       :name name}})
              response (<! request)]
          (if (= (:status response) 200)
            (let [upload-url (-> response :body :uploadUrl)]
              (go
                (let [progress-chan (chan)
                      request (http/put upload-url
                                        {:multipart-params  [[name file]]
                                         :with-credentials? false
                                         :content-type      type
                                         :progress          progress-chan})]
                  (go-loop []
                           (when-let [msg (<! progress-chan)]
                             (swap! upload-progress assoc :percentage (calc-percent msg))
                             (recur)))
                  (let [{success :success} (<! request)]
                    (swap! upload-progress assoc :complete success)))))
            (js/alert (-> response :body :error))))))))

(defn content-editable [label & [opts]]
  [:span.editable
   {:width            (or (:width opts) 159)
    :content-editable true
    :style            (:style opts)
    :on-change        #(handle-text-input % :client :name)}
   label])

(defn connect-view []
  [:div.wrapper
   [:div.left
    [:div.w-80.text.w-100-small.connect
     [:h1 [:span "CONNECT"]]
     [:div {:class "animated fadeIn"
            :style {:display (if (-> @forms :client :show)
                               "block" "none")}}
      [:p#connect-form "Hello, I'm "
       [content-editable "your name"]
       " and I would like to be contacted at "
       [content-editable "your e-mail/phone number"]
       " to collaborate on a project called "
       [content-editable "project name" {:width 80}]
       ". Here are the details: "
       [content-editable "__________________" {:width 80 :style {:text-decoration "underline"}}]]]

     [:div {:class "animated fadeIn"
            :style {:display (if (-> @forms :job :show)
                               "block" "none")}}
      [:p "Hello, I'm "
       [content-editable "your name" {:width 65}]
       " and I would like to learn about employment opportunities at Forten Media. I can be contacted at "
       [content-editable "your e-mail/phone number" {:width "159px"}]

       ". My resume is "
       [:span.file-input-outer-wrap
        [:span.file-input-inner-wrap
         [:input {:type      "file"
                  :name      ""
                  :on-change #(apply handle-upload [% upload-progress])}]]
        "attached here"
        (let [{percentage :percentage complete :complete} @upload-progress]
          (cond
            complete [:span " " [:i.far.fa-check-circle]]
            (> percentage 0) (str " (" percentage "%)")))]

       ". My demo reel can be accessed at "
       [content-editable "this link" {:width 47}] "."]

      [:div
       {:class "textarea animated fadeIn"
        :style {:display (if (every? empty? (-> @forms :job :fields vals))
                           "none" "block")}}
       "A brief cover letter is below:" [:br]
       [:textarea {:rows      "3"
                   :on-change #(handle-text-input % :job :cover-letter)}]]]

     [footer-large "410"]]]

   [:div.right
    [:div.display
     [:iframe
      {:allowFullScreen "allowfullscreen",
       :frameBorder     "0",
       :src             "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3310.2443596136322!2d-84.362234784788!3d33.93484268063799!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x88f50eaa1c513eff%3A0x82510a80491c2974!2s1+Glenlake+Pkwy+NE+%23650%2C+Atlanta%2C+GA+30328!5e0!3m2!1sen!2sus!4v1548122891549"}]

     [footer-small "410"]]]])

(ns forten-media.views.connect
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as r]
            [cljs.core.async :refer [<! timeout chan >! go-loop]]
            [cljs-http.client :as http]
            [forten-media.components.footer :refer [footer-large footer-small]]
            [komponentit.autosize :as autosize]))

(def serverless-endpoint "https://ebxldex1g6.execute-api.us-west-2.amazonaws.com/dev/s3-url")

(def subject-lines {:client "Connect: Potential Client"
                    :job    "Connect: Employment Inquiry"})

(def upload-progress
  (r/atom {:percentage 0
           :complete   false}))

(def init-state
  {:s3-key nil
   :type   #{:client :job}
   :fields {:name         ""
            :email        ""
            :phone        ""
            :project      ""
            :details      ""
            :resume-file  ""
            :demo-link    ""
            :cover-letter ""}})

(def form-state
  (r/atom init-state))

(defn- calc-percent [upload]
  (let [{loaded :loaded total :total} upload]
    (when (every? #(> % 0) [total loaded])
      (js/Math.round (* (/ loaded total) 100)))))

(defn- handle-text-input [e type field]
  (let [val (-> e .-target .-value)]
    (swap! form-state assoc
           :type #{type}
           :fields (merge (:fields @form-state) {field val}))))

(defn- s3-upload [url name file type progress-chan]
  (http/put url
            {:multipart-params  [[name file]]
             :with-credentials? false
             :content-type      type
             :progress          progress-chan}))

(defn- s3-get [key-name]
  (http/post serverless-endpoint
             {:content-type      "application/json"
              :with-credentials? false
              :json-params       {:key       key-name
                                  :operation "getObject"}}))

(defn- s3-post [type size name]
  (http/post
    serverless-endpoint
    {:content-type      "application/json"
     :with-credentials? false
     :json-params       {:type      type
                         :size      size
                         :name      name
                         :operation "putObject"}}))

(defn- prepare-email-body [type]
  (let [{:keys [name email phone project
                details resume-file
                demo-link cover-letter]} (:fields @form-state)]
    (if (:client type)
      (str
        "<p>Hello, I'm <b>" name "</b> and I would like to be contacted at " email " / " phone "<br/>"
        " to collaborate on a project called <b>" project "</b>." "</p>"
        "<p><b>Here are the details: </b>" "<br/>" details "</p>")
      (str
        "<p>Hello, I'm <b>" name "</b> and I would like to learn about employment opportunities at Forten Media." "</p>"
        "<p>I can be contacted at " email " / " phone  "</p>"
        "<p>My <b>resume</b> is attached <a href='" resume-file "'>here</a></p>"
        "<p>My <b>demo reel</b> can be accessed at " demo-link "</p>"
        "<p>A brief <b>cover letter</b> is below:" "</p>"
        "<p>" cover-letter "</p>"))))

(defn- prepare-email-subject [type]
  (if (:client type)
    (:client subject-lines)
    (:job subject-lines)))

(defn- mmm-api-post []
  (http/post
    "http://api.mmmanyfold.com/api/mail"
    {:with-credentials? false
     :json-params       {:from    (-> @form-state :fields :email)
                         :to      "create@fortenmedia.com"
                         :subject (prepare-email-subject (-> @form-state :type))
                         :body    (prepare-email-body (-> @form-state :type))
                         :domain  "copa.mmmanyfold.com"}}))

(defn- is-email-valid? [email]
  (let [regex #"^[^\s@]+@[^\s@]+\.[^\s@]+$"]
    (re-seq regex email)))

(defn- is-form-valid? []
  (let [email (-> @form-state :fields :email)]
    (and (not= @form-state init-state)
         (is-email-valid? email))))

(defn handle-upload [e]
  (when-let [file (aget (-> e .-target .-files) 0)]
    (.preventDefault e)
    (let [type (.-type file)
          size (.-size file)
          name (.-name file)]
      (go
        (let [post-request (s3-post type size name)
              post-response (<! post-request)]
          (if (= (:status post-response) 200)
            (let [{:keys [uploadUrl key]} (post-response :body)]
              (swap! form-state assoc :s3-key key)
              (go
                (let [progress-chan (chan)
                      upload-request (s3-upload uploadUrl name file type progress-chan)]
                  (go-loop []
                           (when-let [msg (<! progress-chan)]
                             (swap! upload-progress assoc :percentage (calc-percent msg))
                             (recur)))
                  (let [{success :success} (<! upload-request)]
                    (swap! upload-progress assoc :complete success)
                    (when success
                      (let [{:keys [success body]} (<! (s3-get (@form-state :s3-key)))]
                        (when success
                          (swap! form-state assoc-in [:fields :resume-file] (:url body)))))))))
            (js/alert (-> post-response :body :error))))))))

(defn reset-state-btn! []
  [:a#reset-btn
   {:class    "animated fadeIn teal4"
    :style    {:display (if (or (= init-state @form-state)
                                (:sent! (-> @form-state :type))) "none" "inline-block")}
    :on-click #(reset! form-state init-state)}
   "< reset"])

(defn submit-btn []
  [:button#send
   {:type     "button"
    :class    "btn animated"
    :disabled (if (is-form-valid?) false true)
    :style    {:display (if (or (= init-state @form-state)
                                (:sent! (-> @form-state :type))) "none" "block")}
    :on-click (fn [_]
                (go
                  (let [email-request (mmm-api-post)
                        email-response (<! email-request)]
                    (if (= (:status email-response) 200)
                      (swap! form-state assoc :type #{:sent!})
                      (js/alert (:body email-response))))))}
   "SEND"])

(defn connect-view []
  (let [_ (reset! form-state init-state)]
    (fn []
      [:div.wrapper
       [:div.left
        [:div.w-80.text.w-100-small.connect

         [:h1 {:style {:margin-bottom "0"}}
          [:span "CONNECT"]]

         [reset-state-btn!]

         ;; :client sub-view
         ;; ----------------

         [:div {:class "animated fadeIn"
                :style {:margin-top "2em"
                        :display (if (:client (-> @form-state :type)) "block" "none")}}
          [:p "Hello, I'm "
           [autosize/input {:placeholder "your name"
                            :class       "animated fadeIn"
                            :style       {:width 65}
                            :value       (-> @form-state :fields :name)
                            :on-change   #(handle-text-input % :client :name)}]
           " and I would like to be contacted at "
           [autosize/input {:placeholder "your e-mail"
                            :type        "email"
                            :class       "animated fadeIn"
                            :style       {:width 159}
                            :value       (-> @form-state :fields :email)
                            :on-change   #(handle-text-input % :client :email)}]
           [:span.teal4 " / "]
           [autosize/input {:placeholder "phone number"
                            :class       "animated fadeIn"
                            :style       {:width 159}
                            :value       (-> @form-state :fields :phone)
                            :on-change   #(handle-text-input % :client :phone)}]
           " to collaborate on a project called "
           [autosize/input {:placeholder "project name"
                            :class       "animated fadeIn"
                            :style       {:width 80}
                            :value       (-> @form-state :fields :project)
                            :on-change   #(handle-text-input % :client :project)}]
           ". Here are the details: "
           [autosize/input {:placeholder "__________________"
                            :class       "animated fadeIn"
                            :style       {:width 80 :text-decoration "underline"}
                            :value       (-> @form-state :fields :details)
                            :on-change   #(handle-text-input % :client :details)}]]]

         ;; :job sub-view
         ;; -------------

         [:div {:class "animated fadeIn"
                :style {:margin-top "2em"
                        :display (if (:job (-> @form-state :type)) "block" "none")}}
          [:p "Hello, I'm "
           [autosize/input {:placeholder "your name"
                            :class       "animated fadeIn"
                            :style       {:width 65}
                            :value       (-> @form-state :fields :name)
                            :on-change   #(handle-text-input % :job :name)}]

           " and I would like to learn about employment opportunities at Forten Media. I can be contacted at "
           [autosize/input {:placeholder "your e-mail"
                            :class       "animated fadeIn"
                            :type        "email"
                            :style       {:width 159}
                            :value       (-> @form-state :fields :email)
                            :on-change   #(handle-text-input % :job :email)}]
           [:span.teal4 " / "]
           [autosize/input {:placeholder "phone number"
                            :class       "animated fadeIn"
                            :style       {:width 159}
                            :value       (-> @form-state :fields :phone)
                            :on-change   #(handle-text-input % :job :phone)}]
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
           [autosize/input {:placeholder "this link"
                            :class       "animated fadeIn"
                            :style       {:width 47}
                            :value       (-> @form-state :fields :demo-link)
                            :on-change   #(handle-text-input % :job :demo-link)}] "."]

          [:div
           {:class "textarea animated fadeIn"
            :style {:display (if (every? empty? (-> @form-state :fields vals))
                               "none" "block")}}
           "A brief cover letter is below:" [:br]
           [autosize/textarea
            {:min-rows  3
             :max-rows  15
             :value     (-> @form-state :fields :cover-letter)
             :on-change #(handle-text-input % :job :cover-letter)}]]]

         ;; :sent! sub-view

         [:div {:class "animated fadeIn"
                :style {:display (if (:sent! (-> @form-state :type)) "block" "none")}}
          [:p.tc "Thanks for connecting! Your message has been sent."]]

         [submit-btn]

         [footer-large "410"]]]

       [:div.right
        [:div.display
         [:iframe
          {:allowFullScreen "allowfullscreen"
           :frameBorder     "0"
           :src             "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3310.2443596136322!2d-84.362234784788!3d33.93484268063799!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x88f50eaa1c513eff%3A0x82510a80491c2974!2s1+Glenlake+Pkwy+NE+%23650%2C+Atlanta%2C+GA+30328!5e0!3m2!1sen!2sus!4v1548122891549"}]

         [footer-small "410"]]]])))

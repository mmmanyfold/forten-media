(ns forten-media.views.home)


(defn home-view []
  [:div.wrapper
    [:div.left.hide-small
     [:div.display.w-70
      [:a {:href "https://vimeo.com/fortenmedia"}
       [:img {:src "../img/demo-reel.png"}]
       [:h2 "demo reel, " [:span "2017"]]]
      [:h3 "forten media"]
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
       [:h1 [:a {:href "#/about"}
             "about"]]]
      [:div.chapter
       [:h1.number "14"]
       [:h1 [:a {:href "https://vimeo.com/fortenmedia"}
             [:span "our " ] "work"]]]
      [:div.chapter
       [:h1.number "410"]
       [:h1 [:a {:href "#/connect"}
             "connect"]]]]]])

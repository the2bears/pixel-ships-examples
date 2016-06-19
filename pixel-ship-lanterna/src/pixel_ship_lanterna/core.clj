(ns pixel-ship-lanterna.core
  (:require [lanterna.screen :as console])
  (:gen-class))

(def scr (console/get-screen :text))

(defn -main
  [& args]
    (do (console/start scr)
      (console/put-string scr 10 10 "Hello, pixel-ships!")
      (console/put-string scr 10 11 "Press any key to exit!")
      (console/redraw scr)
      (console/get-key-blocking scr)
    (console/stop scr)))



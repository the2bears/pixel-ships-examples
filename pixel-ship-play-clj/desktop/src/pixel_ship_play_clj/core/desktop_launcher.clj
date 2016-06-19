(ns pixel-ship-play-clj.core.desktop-launcher
  (:require [pixel-ship-play-clj.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. pixel-ship-play-clj-game "pixel-ship-play-clj" 800 600)
  (Keyboard/enableRepeatEvents true))


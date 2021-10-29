# descript-to-video

plaintextやmarkdown形式のテキストから各種TTSライブラリを使用した動画に変換する

# ツールの構成
* yarn
* marp(markdown->スライドの変換に利用)
  * standalone版にしたほうがよいか?
    * →してた サンプル以下(1920*1080)
    * yarn marp --image-scale 1.5 --images png -o E://Videos/VoiceroidWaveFiles/  resources/slides/template/template.md
* tts-controller(テキストから音声合成エンジンを呼び出すのに使用)
  * ->各種音声合成エンジン
* このツール本体(markdownのparse、aviutlで使用できるobjファイルのエクスポートに利用)
* aviutl+拡張編集プラグイン(今のところは)
  * そのうち他ツール対応させたい(DaVinchiとか)

## インストール

Download from http://example.com/FIXME.

## 使い方

javaの実行環境が必要です

    $ javaw -jar descript-to-video-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## 例

...

## 動作のしくみ
* clojureでmarkdownからttsで読み上げする内容の含まれている行を取得
* marpでmarkdownからスライドを出力
  * 先頭行から順にテキスト内容をspeechSampleを使用して出力
<!-- 
この状態だと音声のみ追加されている状態のはず
スライドをタイムラインに追加する処理が必要
立ち絵もこの状態ではないはず? 
markdown(テキスト+順序情報)+デフォルト値で動画にする
-->
### 既知のバグ

**このプロジェクトはpre-alphaです**

## Motivation
解説動画を楽して作りたい、原稿ファイル一つ用意してバッチに投げ込んだらとりあえずスライドショーに音声がついてる程度の動画ができてるといいのでは?
## TODO/検討事項
TODOというか検討事項と対応状況の書き散らし
 - [x] markdonwから音声を一括保存する
 - [ ] 録音元の制約
   - [ ] スピーカーで鳴らしている音をそのまま録音している以上、ttsソフト以外で音を鳴らしているとそのまま録音されてしまう
 - [ ] 対応しているテキストのフォーマットを増やす
   - [ ] markdown
   - [ ] csv
 - [ ] 呼び出し元を追加する(少なくともターミナルはNG)
   - [ ] aviutl拡張?
   - [ ] batchfile?
   - [ ] ごちゃまぜドロップス拡張?
     - [ ] どのみちc#使うんだし呼び出し元はc#にしておく?
   - [ ] とりあえずgrpcのサーバーとしてバックエンドを書く、フロントは多分grpcの呼び出し元であればいいのでなんとでもなるはずだ(適当)
 - [x] markdownと拡張編集オブジェクトファイルの相互変換
   - [x] できました(yamlと相互変換)
   - [x] 拡張編集のオブジェクトごとにaliasを作る
     - [x] aliasだと立ち絵関係に対応できないのでテンプレの.obj
 - [ ] 依存関係の整理
   - [x] 依存先のリポジトリをこっちに含める?
   - [ ] 現状では一部処理が外部のリポジトリ(ttsController)に依存、リリースを作る際にあっちのビルド後をこっちに含めるか検討
 - [ ] aviutl以外への対応
   - [ ] やるならDaVinchi Resolve?
 - [x] 呼び出し関係
   - [x] marpの呼び出し(yarn使用)
 - [ ] ttsController呼び出しをいい感じにする
   - [ ] grpc使うとか
   - [ ] とりあえず:現状としては別途リポジトリをcloneした上でプロジェクトをビルドしてもらわないといけない
 - [x] テストの追加
 - [ ] エラーハンドリングをちゃんとやる
 - [x] テンプレートを使えるようにする
 - [ ] 設定変更にUIをつける
 - [ ] フォルダ構成を考える
   - [ ] .aupと参照先のリソースは同一フォルダに入れる。
   - [ ] .mdも同様に.aupと同一フォルダに入れるか?
 - [ ] これ自体のサンプルスライド

## License

EPL 2.0準拠です

Copyright © 2021 simana

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.

# descript-to-video

plaintextやmarkdown形式のテキストから各種TTSライブラリを使用した動画に変換する

## インストール

Download from http://example.com/FIXME.

## 使い方

javaの実行環境が必要です

    $ java -jar descript-to-video-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## 例

...

### 既知のバグ

**このプロジェクトはpre-alphaです**

### Any Other Sections
### That You Think
### Might be Useful

## Motivation
解説動画を楽して作りたい、原稿ファイル一つ用意してバッチに投げ込んだらとりあえずスライドショーに音声がついてる程度の動画ができてるといいのでは?
## TODO
 - [x] markdonwから音声を一括保存する
 - [ ] 対応しているテキストのフォーマットを増やす
 - [ ] 呼び出し元を追加する(少なくともターミナルはNG)
   - [ ] aviutl拡張?
   - [ ] batchfile?
   - [ ] ごちゃまずドロップス拡張?
 - [ ] markdownと拡張編集オブジェクトファイルの相互変換
   - [ ] 拡張編集のオブジェクトごとにaliasを作る
 - [ ] 依存関係の整理
   - [ ] 依存先のリポジトリをこっちに含める?
 - [ ] aviutl以外への対応
   - [ ] やるならDaVinchi Resolve
 - [ ] ttsController呼び出しをいい感じにする
   - [ ] grpc使うとか
 - [ ] テストの追加
 - [ ] エラーハンドリングをちゃんとやる
 - [ ] テンプレートを使えるようにする
 - [ ] 設定変更にUIをつける

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

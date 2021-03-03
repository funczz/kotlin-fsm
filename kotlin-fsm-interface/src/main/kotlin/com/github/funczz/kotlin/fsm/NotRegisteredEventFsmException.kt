package com.github.funczz.kotlin.fsm

/**
 * 例外エラー
 * 状態に登録されていないイベントを発火した
 * @author funczz
 */
class NotRegisteredEventFsmException(
        /**
         * 例外エラーに付与するメッセージ
         */
        message: String
) : Exception(message)
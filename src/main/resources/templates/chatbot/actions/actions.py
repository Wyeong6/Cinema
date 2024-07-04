from rasa_sdk import Action, Tracker
from rasa_sdk.executor import CollectingDispatcher
from rasa_sdk.events import UserUtteranceReverted, SlotSet, Restarted

class ActionDefaultFallback(Action):
    def name(self):
        return "action_default_fallback"

    async def run(self, dispatcher, tracker, domain):
        dispatcher.utter_message(text="I didn't quite understand that. Could you rephrase?")
        return [Restarted()] # 봇 재시작
#        return [UserUtteranceReverted()]  # 봇 재시작 대신 사용자 입력을 취소하고 이전 상태로 되돌림

class ActionCompletionRestartedBot(Action):
    def name(self):
        return "action_completion_restarted_bot"

    async def run(self, dispatcher, tracker, domain):
        return [Restarted()] # 봇 재시작
import os
import glob
import asyncio
from googletrans import Translator
from rasa.core.channels.channel import CollectingOutputChannel, UserMessage
from rasa.core.agent import Agent
from rasa.core.utils import EndpointConfig
from flask import Flask, render_template, Blueprint, jsonify, request, Response
from flask_cors import CORS, cross_origin

# Flask 앱 생성
app = Flask(__name__)
#CORS(app)  # CORS 미들웨어 추가
CORS(app, resources={r"/webhook/*": {"origins": "http://localhost:5004"}}) #특정 origin의 요청을 허용

# Rasa 에이전트 설정
MODEL_PATH = "models"
action_endpoint = EndpointConfig(url="http://localhost:5055/webhook")
agent = Agent.load(MODEL_PATH, action_endpoint=action_endpoint)

translator = Translator()  # Translator 객체를 여기서 생성

class MyNewInput:
    @classmethod
    def name(cls):
        return "rasa"

    def _check_token(self, token):
        if token == 'secret':
            return {'username': 1234}
        else:
            print("Failed to check token: {}.".format(token))
            return None

    def blueprint(self, on_new_message):
        templates_folder = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'myown_chatbot')

        custom_webhook = Blueprint('custom_webhook', __name__, template_folder=templates_folder)

        @custom_webhook.route("/", methods=['GET'])
        def health():
            return jsonify({"status": "ok"})

        @custom_webhook.route("/chat", methods=['GET'])
        def chat():
            return render_template('chatbot.html')

        @custom_webhook.route("/chat", methods=['POST'])
        def receive():
            sender_id = request.json.get('sender', '')  # sender_id를 request에서 추출
            text = request.json.get('message', '')  # message를 request에서 추출

            # 사용자가 입력한 한글 텍스트를 영어로 번역
            translated_text = translator.translate(text, src='ko', dest='en').text

            collector = CollectingOutputChannel()
            asyncio.run(agent.handle_text(translated_text, collector, sender_id))  # 비동기 함수 호출

            # Rasa로부터 받은 응답을 한글로 번역
            messages = []
            for message in collector.messages:
                translated_message = translator.translate(message['text'], src='en', dest='ko').text
                messages.append({'text': translated_message})

            return jsonify(messages)

        return custom_webhook

input_channel = MyNewInput()
custom_webhook = input_channel.blueprint(agent.handle_text)

# Blueprint 등록
app.register_blueprint(custom_webhook, url_prefix='/webhook')

if __name__ == "__main__":
    # Flask 앱 실행
    port = int(os.environ.get('PORT', 5004))
    app.run(host='0.0.0.0', port=port)

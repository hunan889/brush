#include <jni.h>
#include "./tc_tea.h"

extern "C" {

JNIEXPORT jbyteArray JNICALL Java_com_duowan_android_base_Tea_encrypt2(
		JNIEnv* env, jobject thiz, jbyteArray key, jbyteArray in);

JNIEXPORT jbyteArray JNICALL Java_com_duowan_android_base_Tea_decrypt2(
		JNIEnv* env, jobject thiz, jbyteArray key, jbyteArray in);

}

static const char* DEFAULT_KEY = "ABCDEFGHIJKLMNOP";

jbyteArray Java_com_duowan_android_base_Tea_encrypt2(JNIEnv* env, jobject thiz,
		jbyteArray key, jbyteArray in) {
	bool result = false;
	jbyte* pIn = NULL;
	jbyte* pKey = NULL;
	jbyteArray out = NULL;
	vector<char> v;

	if (!in) {
		goto DONE;
	}

	if (key && env->GetArrayLength(key) != 16) {
		goto DONE;
	}

	if (!(pIn = env->GetByteArrayElements(in, NULL))) {
		goto DONE;
	}

	if (key && !(pKey = env->GetByteArrayElements(key, NULL))) {
		goto DONE;
	}

	try {
		v = taf::TC_Tea::encrypt2(pKey ? (char*) pKey : DEFAULT_KEY,
				(char*) pIn, env->GetArrayLength(in));
		result = true;
	} catch (...) {
	}

	DONE:

	if (key && pKey) {
		env->ReleaseByteArrayElements(key, pKey, JNI_ABORT);
	}
	if (in && pIn) {
		env->ReleaseByteArrayElements(in, pIn, JNI_ABORT);
	}

	if (result) {
		out = env->NewByteArray(v.size());
		if (out) {
			env->SetByteArrayRegion(out, 0, v.size(), (jbyte*) &v[0]);
		}
	}
	return out;
}

jbyteArray Java_com_duowan_android_base_Tea_decrypt2(JNIEnv* env, jobject thiz,
		jbyteArray key, jbyteArray in) {
	bool result = false;
	jbyte* pIn = NULL;
	jbyte* pKey = NULL;
	jbyteArray out = NULL;
	vector<char> v;

	if (!in) {
		goto DONE;
	}

	if (key && env->GetArrayLength(key) != 16) {
		goto DONE;
	}

	if (!(pIn = env->GetByteArrayElements(in, NULL))) {
		goto DONE;
	}

	if (key && !(pKey = env->GetByteArrayElements(key, NULL))) {
		goto DONE;
	}

	try {
		v = taf::TC_Tea::decrypt2(pKey ? (char*) pKey : DEFAULT_KEY,
				(char*) pIn, env->GetArrayLength(in));
		result = true;
	} catch (...) {
	}

	DONE:

	if (key && pKey) {
		env->ReleaseByteArrayElements(key, pKey, JNI_ABORT);
	}
	if (in && pIn) {
		env->ReleaseByteArrayElements(in, pIn, JNI_ABORT);
	}

	if (result) {
		out = env->NewByteArray(v.size());
		if (out) {
			env->SetByteArrayRegion(out, 0, v.size(), (jbyte*) &v[0]);
		}
	}
	return out;
}


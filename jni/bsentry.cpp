#include "stdlib.h"
#include "nativehelper\JNIHelp.h"
#include "utils\Log.h"
#include "bsentry.h"

#define APP_PKG "com/rhodes/bsdiffdemo/component/bsdiff"



#define REGISTER_NATIVE(env, module) { \
	extern int register_##module(JNIEnv *); \
	if (register_##module(env) < 0) \
	return JNI_FALSE; \
}

/*
* Register native methods for all classes we know about.
*
* returns JNI_TRUE on success.
*/
static int registerNatives(JNIEnv* env)
{
	REGISTER_NATIVE(env, Bsdiffer);

	return JNI_TRUE;
}


// ----------------------------------------------------------------------------

/*
* This is called by the VM when the shared library is first loaded.
*/

typedef union {
	JNIEnv* env;
	void* venv;
} UnionJNIEnvToVoid;


__attribute__((visibility("default")))
	jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	UnionJNIEnvToVoid uenv;
	uenv.venv = NULL;
	jint result = -1;
	JNIEnv* env = NULL;

	LOGI("JNI_OnLoad");

	if (vm->GetEnv(&uenv.venv, JNI_VERSION_1_4) != JNI_OK) {
		LOGE("ERROR: GetEnv failed");
		goto bail;
	}
	env = uenv.env;

	if (registerNatives(env) != JNI_TRUE) {
		LOGE("ERROR: registerNatives failed");
		goto bail;
	}

	result = JNI_VERSION_1_4;

bail:
	return result;
}

// ----------------------------------------------------------------------------
static jint Bsdiffer_diff(JNIEnv *env, jobject self, jstring old_pkg, jstring new_pkg, jstring patch_pkg)
{
	int argc = 4;
	char * argv[argc];
	argv[0]="bsdiff";  
	argv[1]=(char*)env->GetStringUTFChars(old_pkg, NULL);  
	argv[2]=(char*)env->GetStringUTFChars(new_pkg, NULL);  
	argv[3]=(char*)env->GetStringUTFChars(patch_pkg, NULL);  

	int ret=applyBsdiff(argc,argv); 

	env->ReleaseStringUTFChars(old_pkg,argv[1]);  
	env->ReleaseStringUTFChars(new_pkg,argv[2]);
	env->ReleaseStringUTFChars(patch_pkg,argv[3]);  
	return ret;
}

static jint Bsdiffer_patch(JNIEnv *env, jobject self, jstring old_pkg, jstring new_pkg, jstring patch_pkg)
{
	int argc = 4;
	char * argv[argc];
	argv[0]="bspatch";  
	argv[1]=(char*)env->GetStringUTFChars(old_pkg, NULL);  
	argv[2]=(char*)env->GetStringUTFChars(new_pkg, NULL);  
	argv[3]=(char*)env->GetStringUTFChars(patch_pkg, NULL);  

	LOGI("params={%s,%s,%s,%s}",argv[0],argv[1],argv[2],argv[3]);
	int ret=applyBspatch(argc, argv);
	LOGI("apply patch result=%d",ret);

	env->ReleaseStringUTFChars(old_pkg,argv[1]);  
	env->ReleaseStringUTFChars(new_pkg,argv[2]);
	env->ReleaseStringUTFChars(patch_pkg,argv[3]);  
	return ret;
}

int register_Bsdiffer(JNIEnv *env)
{
	static const JNINativeMethod methods[] = {
		{ "diff", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I", (void *) Bsdiffer_diff },
		{ "patch", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I", (void *) Bsdiffer_patch },
	};

	return jniRegisterNativeMethods(env, APP_PKG "/Bsdiffer", methods, NELEM(methods));
}
#include "com_softeq_buzz_CameraView.h"
#include <android/bitmap.h>

inline int32_t toInt(jbyte pValue) {
    return (0xff & (int32_t) pValue);
}

inline int32_t max(int32_t pValue1, int32_t pValue2) {
    if (pValue1 < pValue2) {
        return pValue2;
    } else {
        return pValue1;
    }
}

inline int32_t clamp(int32_t pValue, int32_t pLowest,
    int32_t pHighest) {
    if (pValue < 0) {
        return pLowest;
    } else if (pValue > pHighest) {
        return pHighest;
    } else {
        return pValue;
    }
}

inline int32_t color(pColorR, pColorG, pColorB) {
     return 0xFF000000 | ((pColorB << 6)  & 0x00FF0000)
                       | ((pColorG >> 2)  & 0x0000FF00)
                       | ((pColorR >> 10) & 0x000000FF);
}

JNIEXPORT void JNICALL Java_com_softeq_buzz_CameraView_decode
 (JNIEnv * pEnv, jclass pClass, jobject pTarget, jbyteArray pSource){
    // Retrieves bitmap information and locks it for drawing.
    AndroidBitmapInfo lBitmapInfo;
    if (AndroidBitmap_getInfo(pEnv, pTarget, &lBitmapInfo) < 0) {
        return;
    }
    if (lBitmapInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        return;
    }

    uint32_t* lBitmapContent;
    if (AndroidBitmap_lockPixels(pEnv, pTarget,
                                 (void**)&lBitmapContent) < 0) {
        return;
    }

    // Accesses source array data.
    jbyte* lSource = (*pEnv)->GetPrimitiveArrayCritical(pEnv,
                                                        pSource, 0);
    if (lSource == NULL) {
        return;
    }

    int32_t lFrameSize = lBitmapInfo.width * lBitmapInfo.height;
    int32_t lYIndex, lUVIndex;
    int32_t lX, lY;
    int32_t lColorY, lColorU, lColorV;
    int32_t lColorR, lColorG, lColorB;
    int32_t y1192;

    // Processes each pixel and converts YUV to RGB color.
    // Algorithm originating from Ketai open source project.
    // See http://ketai.googlecode.com/.
    for (lY = 0, lYIndex = 0; lY < lBitmapInfo.height; ++lY) {
        lColorU = 0; lColorV = 0;
        // Y is divided by 2 because UVs are subsampled vertically.
        // This means that two consecutives iterations refer to the
        // same UV line (e.g when Y=0 and Y=1).
        lUVIndex = lFrameSize + (lY >> 1) * lBitmapInfo.width;

        for (lX = 0; lX < lBitmapInfo.width; ++lX, ++lYIndex) {
            // Retrieves YUV components. UVs are subsampled
            // horizontally too, hence %2 (1 UV for 2 Y).
            lColorY = max(toInt(lSource[lYIndex]) - 16, 0);
            if (!(lX % 2)) {
                lColorV = toInt(lSource[lUVIndex++]) - 128;
                lColorU = toInt(lSource[lUVIndex++]) - 128;
            }

            // Computes R, G and B from Y, U and V.
            y1192 = 1192 * lColorY;
            lColorR = (y1192 + 1634 * lColorV);
            lColorG = (y1192 - 833  * lColorV - 400 * lColorU);
            lColorB = (y1192 + 2066 * lColorU);

            lColorR = clamp(lColorR, 0, 262143);
            lColorG = clamp(lColorG, 0, 262143);
            lColorB = clamp(lColorB, 0, 262143);

            // Combines R, G, B and A into the final pixel color.
            lBitmapContent[lYIndex] = color(lColorR,lColorG,lColorB);
        }
    }

    // Unlocks the bitmap and releases the Java array when finished.
    (*pEnv)-> ReleasePrimitiveArrayCritical(pEnv,pSource,lSource, 0);
    AndroidBitmap_unlockPixels(pEnv, pTarget);
}

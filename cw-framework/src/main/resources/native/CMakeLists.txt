cmake_minimum_required(VERSION 3.0)
project(timelock)

# 设置C++标准
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# 设置输出目录
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)

# 添加源文件
add_library(timelock SHARED timelock.cpp)

# 为Windows设置导出符号
if(WIN32)
    target_compile_definitions(timelock PRIVATE -DBUILDING_DLL)
endif()

# 安装
install(TARGETS timelock
        RUNTIME DESTINATION bin
        LIBRARY DESTINATION lib) 
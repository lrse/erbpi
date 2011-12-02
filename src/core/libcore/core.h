#ifndef __LIBCORE_H__
#define __LIBCORE_H__

#include <string>

void core_initialize(const std::string& log_filename);
void core_deinitialize(void);

bool core_start(const std::string& xml_string);
void core_stop(void);

void core_execute(void);

#endif 

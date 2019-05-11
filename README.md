# pdf2jrxml
by Hitoshi Ozawa

Tool to generate jasperreports jrxml file from pdf file.

Current version only supports generating a single page form.

## Currently Supported Mappings
pdf     JasperReports
text -> static text
input field -> text field
line -> line
rectangle -> rectangle

# Requirements
   JDK 8 or higher

# Running a Sample
Execute the following maven command to compile and run the included sample.
1. mvn package
1. ch target
1. ./pdf2jrxml.sh

# Commandline Syntax
   ```
   java -jar pdf2jrxml.sh [-h] [-c <configuration file name>]
        [i <input file names>] [-o <output file names>] 
   ``` 

short | long      |   description
------|-----------|-----------------------------------
  -h, | --help    |   show help
  -c, | --conf    |   configuration filename
  -i, | --input   |   path to input pdf file
  -o, | --output  |   path to generate jrxml file
  

## Configuration File
   Default arguments may be set in properties file (default:pdf2jrxml.properties).
   Values specified by arguments will override values specified in configuration file.
   
   property name     |   value
---------------------|-----------------------------------------------------------------------------
   pdf_filename      |   path to input pdf file
   jrxmlFilename     |   path to generate jrxml file
   margin_left       |   left margin in jrxml (default:0)
   margin_right      |   right margin in jrxml (default:0)
   margin_top        |   top margin in jrxml (default:0)
   margin_bottom     |   bottom maring in jrxml (default:0)
   encoding          |   encoding (default:UTF-8)
   title_default     |   title of jrxml (default:PDF2Jrxml Form)
   font_fallback     |   font to use when font in pdf is not found (default:TIMES_ROMAN)
   
   Example:
   ```
     margin_left=0
     margin_right=0
     margin_top=0
     margin_bottom=0
     encoding=UTF-8
     title_default=PDF2Jrxml Form
     font_fallback=TIMES_ROMAN
   ```
END

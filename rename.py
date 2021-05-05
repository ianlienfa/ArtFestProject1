import os
from shutil import copyfile

os.mkdir('./generated')

for j in range(12):
  for i in range(33):
    copyfile(f'[{i}][{j}].jpg', f'generated/small_image{j*5+i}.jpg')
    # os.rename(f'[{i}][{j}].jpg', f'smallImage{i*10+j}.jpg')
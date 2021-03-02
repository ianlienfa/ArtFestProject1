import os
from shutil import copyfile

os.mkdir('./generated')

for j in range(10):
  for i in range(10):
    copyfile(f'[{i}][{j}].jpg', f'generated/small_image{j*10+i}.jpg')
    # os.rename(f'[{i}][{j}].jpg', f'smallImage{i*10+j}.jpg')